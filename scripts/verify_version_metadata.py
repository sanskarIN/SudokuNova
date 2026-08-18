#!/usr/bin/env python3
"""Verify Android versionName/versionCode stay internally consistent."""

from __future__ import annotations

import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
BUILD_FILE = ROOT / "app/build.gradle.kts"


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def main() -> None:
    text = BUILD_FILE.read_text(encoding="utf-8")
    code_match = re.search(r"\bversionCode\s*=\s*(\d+)\b", text)
    name_match = re.search(r'\bversionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', text)
    if code_match is None:
        fail("app/build.gradle.kts must declare a numeric versionCode")
    if name_match is None:
        fail("versionName must use major.minor.patch numeric Semantic Versioning")

    version_code = int(code_match.group(1))
    major, minor, patch = (int(value) for value in name_match.groups())
    if minor > 99 or patch > 99:
        fail("minor and patch components must fit the versionCode convention (0..99)")

    expected_code = major * 10_000 + minor * 100 + patch
    if version_code != expected_code:
        fail(
            f"versionCode {version_code} does not match versionName "
            f"{major}.{minor}.{patch}; expected {expected_code}",
        )
    if version_code <= 0:
        fail("versionCode must be positive")

    print(
        f"Android version metadata verified: {major}.{minor}.{patch} "
        f"(versionCode {version_code}).",
    )


if __name__ == "__main__":
    main()
