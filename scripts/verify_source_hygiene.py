#!/usr/bin/env python3
"""Reject debug-only output and release-blocking source escapes in production code."""

from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
SOURCE_ROOTS = (
    ROOT / "app/src/main/java",
    ROOT / "sudoku-engine/src/main/kotlin",
)

FORBIDDEN = {
    "android.util.Log": "Android Log API must not be used by the release base app",
    "Log.d(": "debug logging must not remain in production source",
    "Log.v(": "verbose logging must not remain in production source",
    "System.out.print": "stdout debug output must not remain in production source",
    "System.err.print": "stderr debug output must not remain in production source",
    "printStackTrace(": "raw stack traces must not be printed from production source",
}


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def main() -> None:
    scanned = 0
    for source_root in SOURCE_ROOTS:
        if not source_root.is_dir():
            fail(f"Missing production source directory: {source_root.relative_to(ROOT)}")
        for path in source_root.rglob("*.kt"):
            scanned += 1
            text = path.read_text(encoding="utf-8")
            for token, reason in FORBIDDEN.items():
                if token in text:
                    fail(f"{reason}: {path.relative_to(ROOT)} contains {token!r}")

    if scanned == 0:
        fail("No production Kotlin files were scanned")

    print(f"Production source hygiene verification passed across {scanned} Kotlin files.")


if __name__ == "__main__":
    main()
