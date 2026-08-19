#!/usr/bin/env python3
"""Validate SudokuNova Android release outputs and write SHA-256 evidence.

The normal CI path proves archive structure, expected APK identity/version metadata,
a non-empty R8 mapping file, and deterministic SHA-256/size evidence. A protected
release environment can additionally require APK/AAB signature verification and
bind those artifacts to expected signing-certificate SHA-256 fingerprints without
putting signing credentials in this tool or the repository.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import zipfile

APK_REQUIRED_ENTRIES = {
    "AndroidManifest.xml",
    "classes.dex",
}
AAB_REQUIRED_ENTRIES = {
    "BundleConfig.pb",
    "base/manifest/AndroidManifest.xml",
    "base/dex/classes.dex",
}
SHA256_HEX_RE = re.compile(r"^[0-9a-f]{64}$")
APK_CERT_RE = re.compile(r"certificate SHA-256 digest:\s*([0-9A-Fa-f:]+)", re.IGNORECASE)
KEYTOOL_SHA256_RE = re.compile(r"^\s*SHA256:\s*([0-9A-Fa-f:]+)\s*$", re.MULTILINE)


def require_file(path: Path, label: str) -> None:
    if not path.is_file():
        raise ValueError(f"{label} does not exist: {path}")
    if path.stat().st_size <= 0:
        raise ValueError(f"{label} is empty: {path}")


def validate_zip(path: Path, label: str, required_entries: set[str]) -> None:
    require_file(path, label)
    if not zipfile.is_zipfile(path):
        raise ValueError(f"{label} is not a valid ZIP-based Android artifact: {path}")
    with zipfile.ZipFile(path) as archive:
        bad_member = archive.testzip()
        if bad_member is not None:
            raise ValueError(f"{label} contains a corrupt member: {bad_member}")
        names = set(archive.namelist())
    missing = sorted(required_entries - names)
    if missing:
        raise ValueError(f"{label} is missing required entries: {', '.join(missing)}")


def _load_apk_metadata_payload(path: Path) -> dict[str, object]:
    require_file(path, "APK output metadata")
    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise ValueError(f"APK output metadata is not valid UTF-8 JSON: {path}") from exc
    if not isinstance(payload, dict):
        raise ValueError("APK output metadata root must be an object")
    return payload


def load_apk_metadata(path: Path) -> tuple[int, str]:
    payload = _load_apk_metadata_payload(path)
    elements = payload.get("elements")
    if not isinstance(elements, list) or len(elements) != 1:
        raise ValueError("APK output metadata must contain exactly one release element")
    element = elements[0]
    if not isinstance(element, dict):
        raise ValueError("APK output metadata release element must be an object")

    version_code = element.get("versionCode")
    version_name = element.get("versionName")
    if not isinstance(version_code, int):
        raise ValueError("APK output metadata is missing an integer versionCode")
    if not isinstance(version_name, str) or not version_name:
        raise ValueError("APK output metadata is missing a non-empty versionName")
    return version_code, version_name


def load_apk_application_id(path: Path) -> str:
    payload = _load_apk_metadata_payload(path)
    application_id = payload.get("applicationId")
    if not isinstance(application_id, str) or not application_id.strip():
        raise ValueError("APK output metadata is missing a non-empty applicationId")
    return application_id


def normalize_cert_sha256(value: str) -> str:
    normalized = value.replace(":", "").strip().lower()
    if not SHA256_HEX_RE.fullmatch(normalized):
        raise ValueError("certificate SHA-256 fingerprint must contain exactly 64 hexadecimal digits")
    return normalized


def _run_tool(command: list[str]) -> subprocess.CompletedProcess[str]:
    env = dict(os.environ)
    env["LC_ALL"] = "C"
    env["LANG"] = "C"
    return subprocess.run(
        command,
        capture_output=True,
        text=True,
        check=False,
        env=env,
    )


def _unique_digests(values: list[str]) -> list[str]:
    return sorted({normalize_cert_sha256(value) for value in values})


def parse_apksigner_cert_sha256(output: str) -> list[str]:
    digests = _unique_digests(APK_CERT_RE.findall(output))
    if not digests:
        raise RuntimeError("apksigner verification succeeded but no signer certificate SHA-256 digest was reported")
    return digests


def parse_keytool_cert_sha256(output: str) -> list[str]:
    digests = _unique_digests(KEYTOOL_SHA256_RE.findall(output))
    if not digests:
        raise RuntimeError("keytool did not report a signer certificate SHA-256 fingerprint for the AAB")
    return digests


def _require_expected_digest(actual: list[str], expected: str, label: str) -> None:
    normalized_expected = normalize_cert_sha256(expected)
    if normalized_expected not in actual:
        actual_text = ", ".join(actual)
        raise RuntimeError(
            f"{label} signer certificate SHA-256 mismatch: expected {normalized_expected}, got {actual_text}"
        )


def verify_apk_signature(path: Path, expected_cert_sha256: str | None = None) -> list[str]:
    tool = shutil.which("apksigner")
    if tool is None:
        raise RuntimeError("apksigner is required for mandatory APK signature verification")

    completed = _run_tool([tool, "verify", "--verbose", "--print-certs", str(path)])
    combined = "\n".join(part for part in (completed.stdout, completed.stderr) if part).strip()
    if completed.returncode != 0:
        raise RuntimeError(
            "APK signature verification failed: "
            + (combined or f"apksigner exited with status {completed.returncode}")
        )
    digests = parse_apksigner_cert_sha256(combined)
    if expected_cert_sha256 is not None:
        _require_expected_digest(digests, expected_cert_sha256, "APK")
    return digests


def verify_aab_signature(path: Path, expected_cert_sha256: str | None = None) -> list[str]:
    jarsigner = shutil.which("jarsigner")
    if jarsigner is None:
        raise RuntimeError("jarsigner is required for mandatory AAB signature verification")

    completed = _run_tool([jarsigner, "-verify", "-certs", str(path)])
    combined = "\n".join(part for part in (completed.stdout, completed.stderr) if part).strip()
    normalized = combined.lower()
    verified = (
        completed.returncode == 0
        and "jar verified" in normalized
        and "jar is unsigned" not in normalized
    )
    if not verified:
        raise RuntimeError(
            "AAB signature verification failed: "
            + (combined or f"jarsigner exited with status {completed.returncode}")
        )

    keytool = shutil.which("keytool")
    if keytool is None:
        raise RuntimeError("keytool is required to record the AAB signer certificate SHA-256 fingerprint")
    cert_result = _run_tool([keytool, "-printcert", "-jarfile", str(path)])
    cert_output = "\n".join(part for part in (cert_result.stdout, cert_result.stderr) if part).strip()
    if cert_result.returncode != 0:
        raise RuntimeError(
            "AAB signer certificate inspection failed: "
            + (cert_output or f"keytool exited with status {cert_result.returncode}")
        )
    digests = parse_keytool_cert_sha256(cert_output)
    if expected_cert_sha256 is not None:
        _require_expected_digest(digests, expected_cert_sha256, "AAB")
    return digests


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def display_path(path: Path) -> str:
    try:
        return path.resolve().relative_to(Path.cwd().resolve()).as_posix()
    except ValueError:
        return path.resolve().as_posix()


def write_manifest(paths: list[Path], output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    lines = [
        "# SudokuNova release artifact SHA-256 evidence",
        "# sha256  size_bytes  path",
    ]
    for path in sorted(paths, key=lambda item: display_path(item)):
        lines.append(f"{sha256(path)}  {path.stat().st_size}  {display_path(path)}")
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_signature_evidence(
    output: Path,
    apk_digests: list[str],
    aab_digests: list[str],
) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    lines = [
        "# SudokuNova release signer certificate SHA-256 evidence",
        *(f"apk_signer_sha256={digest}" for digest in apk_digests),
        *(f"aab_signer_sha256={digest}" for digest in aab_digests),
    ]
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")


def parse_args(argv: list[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", required=True, type=Path)
    parser.add_argument("--aab", required=True, type=Path)
    parser.add_argument("--mapping", required=True, type=Path)
    parser.add_argument("--metadata", required=True, type=Path)
    parser.add_argument("--expected-version-code", required=True, type=int)
    parser.add_argument("--expected-version-name", required=True)
    parser.add_argument(
        "--expected-application-id",
        help="expected Android applicationId from APK output metadata",
    )
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument(
        "--require-signatures",
        action="store_true",
        help="require APK verification with apksigner and AAB verification with jarsigner/keytool",
    )
    parser.add_argument(
        "--expected-apk-cert-sha256",
        help="expected APK signer certificate SHA-256 fingerprint (hex, colons optional)",
    )
    parser.add_argument(
        "--expected-aab-cert-sha256",
        help="expected AAB signer/upload certificate SHA-256 fingerprint (hex, colons optional)",
    )
    parser.add_argument(
        "--signature-output",
        type=Path,
        help="write normalized signer certificate SHA-256 evidence when signatures are required",
    )
    return parser.parse_args(argv)


def main(argv: list[str] | None = None) -> int:
    args = parse_args(argv)
    try:
        if (args.expected_apk_cert_sha256 or args.expected_aab_cert_sha256 or args.signature_output) and not args.require_signatures:
            raise ValueError(
                "certificate expectations/signature evidence require --require-signatures"
            )

        validate_zip(args.apk, "Release APK", APK_REQUIRED_ENTRIES)
        validate_zip(args.aab, "Release AAB", AAB_REQUIRED_ENTRIES)
        require_file(args.mapping, "R8 mapping")
        actual_code, actual_name = load_apk_metadata(args.metadata)
        if actual_code != args.expected_version_code:
            raise ValueError(
                f"Unexpected APK versionCode: expected {args.expected_version_code}, got {actual_code}"
            )
        if actual_name != args.expected_version_name:
            raise ValueError(
                f"Unexpected APK versionName: expected {args.expected_version_name!r}, got {actual_name!r}"
            )

        actual_application_id: str | None = None
        if args.expected_application_id is not None:
            actual_application_id = load_apk_application_id(args.metadata)
            if actual_application_id != args.expected_application_id:
                raise ValueError(
                    "Unexpected APK applicationId: "
                    f"expected {args.expected_application_id!r}, got {actual_application_id!r}"
                )

        apk_digests: list[str] = []
        aab_digests: list[str] = []
        if args.require_signatures:
            apk_digests = verify_apk_signature(args.apk, args.expected_apk_cert_sha256)
            aab_digests = verify_aab_signature(args.aab, args.expected_aab_cert_sha256)
            if args.signature_output is not None:
                write_signature_evidence(args.signature_output, apk_digests, aab_digests)

        write_manifest([args.apk, args.aab, args.mapping], args.output)
    except (OSError, ValueError, RuntimeError) as exc:
        print(f"Release output verification failed: {exc}", file=sys.stderr)
        return 1

    identity = ""
    if actual_application_id is not None:
        identity = f", applicationId={actual_application_id}"
    print(
        "Release outputs verified: "
        f"versionCode={actual_code}, versionName={actual_name}{identity}, evidence={args.output}"
    )
    if args.require_signatures:
        print(f"Signature: APK verified; signer SHA-256={','.join(apk_digests)}")
        print(f"Signature: AAB verified; signer SHA-256={','.join(aab_digests)}")
    else:
        print("Signature: not required by this verification run")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
