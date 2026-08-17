#!/usr/bin/env python3
"""Verify that SudokuNova localized string keys exist in English and Hindi."""

from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
VALUES = ROOT / "app" / "src" / "main" / "res" / "values"
VALUES_HI = ROOT / "app" / "src" / "main" / "res" / "values-hi"
PREFIXES = ("v04_", "v05_", "v06_", "difficulty_", "theme_")


def collect(directory: Path) -> dict[str, Path]:
    strings: dict[str, Path] = {}
    for file in sorted(directory.glob("*.xml")):
        root = ET.parse(file).getroot()
        for element in root.findall("string"):
            name = element.attrib.get("name")
            if not name or not name.startswith(PREFIXES):
                continue
            if name in strings:
                raise SystemExit(f"Duplicate localized string key {name!r} in {directory}")
            strings[name] = file
    return strings


def main() -> int:
    english = collect(VALUES)
    hindi = collect(VALUES_HI)

    missing_hindi = sorted(set(english) - set(hindi))
    extra_hindi = sorted(set(hindi) - set(english))

    if missing_hindi:
        print("Missing Hindi resources:", file=sys.stderr)
        for key in missing_hindi:
            print(f"  - {key} ({english[key].name})", file=sys.stderr)
    if extra_hindi:
        print("Hindi-only resources without English base:", file=sys.stderr)
        for key in extra_hindi:
            print(f"  - {key} ({hindi[key].name})", file=sys.stderr)

    if missing_hindi or extra_hindi:
        return 1

    if not english:
        print("No localization keys were found.", file=sys.stderr)
        return 1

    print(f"Translation parity verified for {len(english)} localized string keys.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
