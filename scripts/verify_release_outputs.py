#!/usr/bin/env python3
"""Validate SudokuNova Android release outputs and write SHA-256 evidence.

This verifier is intentionally independent of Android SDK tooling so it can run in
CI after Gradle has produced the release APK/AAB. It proves basic archive
structure, expected APK metadata, a non-empty R8 mapping file, and deterministic
SHA-256/size evidence for the produced artifacts. It does not claim production
signing or device-install verification.
"""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
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


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", required=True, type=Path)
    parser.add_argument("--aab", required=True, type=Path)
    parser.add_argument("--mapping", required=True, type=Path)
    parser.add_argument("--metadata", required=True, type=Path)
    parser.add_argument("--expected-version-code", required=True, type=int)
    parser.add_argument("--expected-version-name", required=True)
    parser.add_argument("--output", required=True, type=Path)
    return parser.parse_args()


def main() -> int:
    args = parse_args()
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
        write_manifest([args.apk, args.aab, args.mapping], args.output)
    except ValueError as exc:
        print(f"Release output verification failed: {exc}", file=sys.stderr)
        return 1

    print(
        "Release outputs verified: "
        f"versionCode={actual_code}, versionName={actual_name}, evidence={args.output}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
