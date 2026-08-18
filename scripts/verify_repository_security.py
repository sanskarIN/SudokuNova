#!/usr/bin/env python3
"""Fail CI when obviously dangerous secret/signing material is committed.

This is intentionally a narrow, deterministic repository guard. It is not a
replacement for provider-side secret scanning or human security review.
"""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

SKIP_DIRS = {
    ".git",
    ".gradle",
    ".idea",
    "build",
    ".kotlin",
}

BLOCKED_SUFFIXES = {
    ".jks",
    ".keystore",
    ".p12",
    ".pfx",
}

BLOCKED_NAMES = {
    "google-services.json",
    "service-account.json",
}

TEXT_PATTERNS = (
    ("PEM private key", re.compile(r"-----BEGIN (?:RSA |EC |DSA |OPENSSH )?PRIVATE KEY-----")),
    ("GitHub token", re.compile(r"\bgh[pousr]_[A-Za-z0-9_]{20,}\b")),
)

MAX_TEXT_BYTES = 2 * 1024 * 1024


def iter_files() -> list[Path]:
    files: list[Path] = []
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        relative = path.relative_to(ROOT)
        if any(part in SKIP_DIRS for part in relative.parts):
            continue
        files.append(path)
    return sorted(files)


def main() -> int:
    violations: list[str] = []

    for path in iter_files():
        relative = path.relative_to(ROOT).as_posix()
        lower_name = path.name.lower()

        if path.suffix.lower() in BLOCKED_SUFFIXES:
            violations.append(f"blocked signing/private-key file: {relative}")
            continue

        if lower_name in BLOCKED_NAMES:
            violations.append(f"blocked credential/config file: {relative}")
            continue

        try:
            if path.stat().st_size > MAX_TEXT_BYTES:
                continue
            raw = path.read_bytes()
            if b"\x00" in raw:
                continue
            text = raw.decode("utf-8")
        except (OSError, UnicodeDecodeError):
            continue

        for label, pattern in TEXT_PATTERNS:
            if pattern.search(text):
                violations.append(f"{label} pattern found in: {relative}")

    if violations:
        print("Repository security verification failed:")
        for violation in violations:
            print(f"- {violation}")
        return 1

    print("Repository security verification passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
