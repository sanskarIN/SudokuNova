#!/usr/bin/env python3
"""Verify Android release artifact integrity and emit SHA-256 checksums.

This tool intentionally does not own signing credentials. It validates ZIP
integrity for APK/AAB files, can verify signatures through Android/JDK tools
when requested, and writes deterministic SHA-256 evidence for release records.
"""

from __future__ import annotations

import argparse
import hashlib
import os
import shutil
import subprocess
import sys
import zipfile
from dataclasses import dataclass
from pathlib import Path

SUPPORTED_SUFFIXES = {".apk", ".aab"}
BUFFER_SIZE = 1024 * 1024


@dataclass(frozen=True)
class ArtifactResult:
    path: Path
    sha256: str
    signature_status: str


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        while chunk := handle.read(BUFFER_SIZE):
            digest.update(chunk)
    return digest.hexdigest()


def verify_zip(path: Path) -> None:
    try:
        with zipfile.ZipFile(path) as archive:
            bad_member = archive.testzip()
    except zipfile.BadZipFile as exc:
        raise ValueError(f"not a valid ZIP-based Android artifact: {path}") from exc
    if bad_member is not None:
        raise ValueError(f"corrupt ZIP entry {bad_member!r} in {path}")


def run_signature_tool(path: Path, require_signature: bool) -> str:
    suffix = path.suffix.lower()
    if suffix == ".apk":
        tool = shutil.which("apksigner")
        command = [tool, "verify", "--verbose", "--print-certs", str(path)] if tool else None
        label = "apksigner"
    else:
        tool = shutil.which("jarsigner")
        # Android app-signing certificates are commonly self-signed. Using
        # jarsigner -strict would reject a valid signed AAB merely because its
        # certificate chain is not rooted in a public CA. Verify signature
        # integrity instead, then require explicit "jar verified" output.
        command = [tool, "-verify", "-certs", str(path)] if tool else None
        label = "jarsigner"

    if command is None:
        if require_signature:
            raise RuntimeError(f"{label} is required to verify {path.name} but was not found on PATH")
        return f"not checked ({label} unavailable)"

    env = dict(os.environ)
    env["LC_ALL"] = "C"
    env["LANG"] = "C"
    completed = subprocess.run(
        command,
        capture_output=True,
        text=True,
        check=False,
        env=env,
    )
    combined_output = "\n".join(part for part in (completed.stdout, completed.stderr) if part).strip()

    if suffix == ".aab":
        normalized = combined_output.lower()
        verified = (
            completed.returncode == 0
            and "jar verified" in normalized
            and "jar is unsigned" not in normalized
        )
    else:
        verified = completed.returncode == 0

    if verified:
        return f"verified with {label}"

    if require_signature:
        details = combined_output or f"{label} exited with status {completed.returncode}"
        raise RuntimeError(f"signature verification failed for {path.name} with {label}: {details}")
    return f"not verified with {label} (artifact may be intentionally unsigned)"


def verify_artifact(path: Path, require_signature: bool = False) -> ArtifactResult:
    resolved = path.expanduser().resolve()
    if not resolved.is_file():
        raise ValueError(f"artifact does not exist or is not a file: {path}")
    if resolved.suffix.lower() not in SUPPORTED_SUFFIXES:
        raise ValueError(f"unsupported artifact type: {resolved.name}; expected .apk or .aab")
    if resolved.stat().st_size <= 0:
        raise ValueError(f"artifact is empty: {resolved}")

    verify_zip(resolved)
    signature_status = run_signature_tool(resolved, require_signature=require_signature)
    return ArtifactResult(
        path=resolved,
        sha256=sha256_file(resolved),
        signature_status=signature_status,
    )


def write_checksums(results: list[ArtifactResult], output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    lines = [f"{result.sha256}  {result.path.name}" for result in sorted(results, key=lambda item: item.path.name)]
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("artifacts", nargs="+", type=Path, help="APK/AAB files to verify")
    parser.add_argument(
        "--require-signature",
        action="store_true",
        help="fail unless each artifact passes platform signature verification",
    )
    parser.add_argument(
        "--checksums-out",
        type=Path,
        help="optional output file for deterministic SHA-256 checksum lines",
    )
    return parser.parse_args(argv)


def main(argv: list[str] | None = None) -> int:
    args = parse_args(sys.argv[1:] if argv is None else argv)
    results: list[ArtifactResult] = []

    try:
        for artifact in args.artifacts:
            results.append(verify_artifact(artifact, require_signature=args.require_signature))
    except (OSError, ValueError, RuntimeError) as exc:
        print(f"Release artifact verification failed: {exc}", file=sys.stderr)
        return 1

    for result in results:
        print(f"OK  {result.path}")
        print(f"    SHA-256: {result.sha256}")
        print(f"    Signature: {result.signature_status}")

    if args.checksums_out:
        try:
            write_checksums(results, args.checksums_out)
        except OSError as exc:
            print(f"Failed to write checksum file: {exc}", file=sys.stderr)
            return 1
        print(f"Checksums written to {args.checksums_out}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
