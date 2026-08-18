#!/usr/bin/env python3
"""Validate SudokuNova Android release outputs and write SHA-256 evidence.

The normal CI path proves archive structure, expected APK metadata, a non-empty
R8 mapping file, and deterministic SHA-256/size evidence. A protected release
environment can additionally require APK/AAB signature verification without
putting signing credentials in this tool or the repository.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
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


def load_apk_metadata(path: Path) -> tuple[int, str]:
    require_file(path, "APK output metadata")
    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise ValueError(f"APK output metadata is not valid UTF-8 JSON: {path}") from exc

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


def verify_apk_signature(path: Path) -> str:
    tool = shutil.which("apksigner")
    if tool is None:
        raise RuntimeError("apksigner is required for mandatory APK signature verification")

    completed = _run_tool([tool, "verify", "--verbose", "--print-certs", str(path)])
    if completed.returncode != 0:
        details = "\n".join(part for part in (completed.stdout, completed.stderr) if part).strip()
        raise RuntimeError(
            "APK signature verification failed: "
            + (details or f"apksigner exited with status {completed.returncode}")
        )
    return "verified with apksigner"


def verify_aab_signature(path: Path) -> str:
    tool = shutil.which("jarsigner")
    if tool is None:
        raise RuntimeError("jarsigner is required for mandatory AAB signature verification")

    completed = _run_tool([tool, "-verify", "-certs", str(path)])
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
    return "verified with jarsigner"


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


def parse_args(argv: list[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", required=True, type=Path)
    parser.add_argument("--aab", required=True, type=Path)
    parser.add_argument("--mapping", required=True, type=Path)
    parser.add_argument("--metadata", required=True, type=Path)
    parser.add_argument("--expected-version-code", required=True, type=int)
    parser.add_argument("--expected-version-name", required=True)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument(
        "--require-signatures",
        action="store_true",
        help="require APK verification with apksigner and AAB verification with jarsigner",
    )
    return parser.parse_args(argv)


def main(argv: list[str] | None = None) -> int:
    args = parse_args(argv)
    try:
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

        signature_statuses: list[str] = []
        if args.require_signatures:
            signature_statuses.append(verify_apk_signature(args.apk))
            signature_statuses.append(verify_aab_signature(args.aab))

        write_manifest([args.apk, args.aab, args.mapping], args.output)
    except (OSError, ValueError, RuntimeError) as exc:
        print(f"Release output verification failed: {exc}", file=sys.stderr)
        return 1

    print(
        "Release outputs verified: "
        f"versionCode={actual_code}, versionName={actual_name}, evidence={args.output}"
    )
    if args.require_signatures:
        for status in signature_statuses:
            print(f"Signature: {status}")
    else:
        print("Signature: not required by this verification run")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
