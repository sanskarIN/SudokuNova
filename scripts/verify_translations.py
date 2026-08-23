#!/usr/bin/env python3
"""Verify English/Hindi string-key parity for Android and shared multiplatform resources."""

from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID_VALUES = ROOT / "app" / "src" / "main" / "res" / "values"
ANDROID_VALUES_HI = ROOT / "app" / "src" / "main" / "res" / "values-hi"
SHARED_VALUES = ROOT / "sharedUI" / "src" / "commonMain" / "composeResources" / "values"
SHARED_VALUES_HI = ROOT / "sharedUI" / "src" / "commonMain" / "composeResources" / "values-hi"
ANDROID_PREFIXES = ("v04_", "v05_", "v06_", "v07_", "difficulty_", "theme_")


def collect(directory: Path, prefixes: tuple[str, ...] | None = None) -> dict[str, Path]:
    """Collect localized string keys from XML files in ``directory``.

    Android's mature resource tree intentionally scopes this guard to the existing feature
    prefixes. The shared multiplatform catalog is purpose-built for player-facing strings,
    so every shared ``<string>`` key participates in parity validation.
    """

    strings: dict[str, Path] = {}
    for file in sorted(directory.glob("*.xml")):
        root = ET.parse(file).getroot()
        for element in root.findall("string"):
            name = element.attrib.get("name")
            if not name:
                continue
            if prefixes is not None and not name.startswith(prefixes):
                continue
            if name in strings:
                raise SystemExit(f"Duplicate localized string key {name!r} in {directory}")
            strings[name] = file
    return strings


def parity_errors(
    english: dict[str, Path],
    hindi: dict[str, Path],
    *,
    label: str,
) -> list[str]:
    """Return human-readable key-parity errors for one resource catalog."""

    errors: list[str] = []
    missing_hindi = sorted(set(english) - set(hindi))
    extra_hindi = sorted(set(hindi) - set(english))

    if missing_hindi:
        errors.append(f"Missing Hindi resources in {label}:")
        errors.extend(f"  - {key} ({english[key].name})" for key in missing_hindi)
    if extra_hindi:
        errors.append(f"Hindi-only resources without English base in {label}:")
        errors.extend(f"  - {key} ({hindi[key].name})" for key in extra_hindi)
    if not english:
        errors.append(f"No localization keys were found in {label}.")

    return errors


def main() -> int:
    catalogs = (
        (
            "Android",
            collect(ANDROID_VALUES, ANDROID_PREFIXES),
            collect(ANDROID_VALUES_HI, ANDROID_PREFIXES),
        ),
        (
            "shared multiplatform UI",
            collect(SHARED_VALUES),
            collect(SHARED_VALUES_HI),
        ),
    )

    errors: list[str] = []
    for label, english, hindi in catalogs:
        errors.extend(parity_errors(english, hindi, label=label))

    if errors:
        print("\n".join(errors), file=sys.stderr)
        return 1

    total = sum(len(english) for _, english, _ in catalogs)
    details = ", ".join(f"{label}: {len(english)}" for label, english, _ in catalogs)
    print(f"Translation parity verified for {total} localized string keys ({details}).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
