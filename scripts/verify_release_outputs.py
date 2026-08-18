#!/usr/bin/env python3
"""Verify CI produced the expected non-empty Android release outputs."""

from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def require_files(pattern: str, label: str, minimum_bytes: int) -> list[pathlib.Path]:
    matches = sorted(ROOT.glob(pattern))
    if not matches:
        fail(f"Missing {label}; expected a file matching {pattern}")
    for path in matches:
        size = path.stat().st_size
        if size < minimum_bytes:
            fail(f"{label} is unexpectedly small ({size} bytes): {path.relative_to(ROOT)}")
    return matches


def main() -> None:
    apks = require_files(
        "app/build/outputs/apk/release/*.apk",
        "release APK",
        minimum_bytes=10_000,
    )
    bundles = require_files(
        "app/build/outputs/bundle/release/*.aab",
        "release Android App Bundle",
        minimum_bytes=10_000,
    )
    mappings = require_files(
        "app/build/outputs/mapping/release/mapping.txt",
        "R8 mapping file",
        minimum_bytes=1,
    )

    signed_like = [path for path in apks + bundles if "signed" in path.name.lower()]
    if signed_like:
        fail(
            "CI verification artifacts must remain unsigned; found suspicious output names: "
            + ", ".join(str(path.relative_to(ROOT)) for path in signed_like),
        )

    print("Release output verification passed.")
    for path in apks + bundles + mappings:
        print(f"- {path.relative_to(ROOT)} ({path.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
