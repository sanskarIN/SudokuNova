#!/usr/bin/env python3
"""Fail when repository Markdown contains a broken local file link.

The checker intentionally validates repository-local file targets only. External URLs and
in-document anchors are outside this deterministic offline gate. Fragments on local file
links are ignored here because heading-anchor generation is renderer-specific; the target
file itself must still exist.
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import sys
from urllib.parse import unquote, urlsplit

INLINE_LINK_RE = re.compile(r"(?<!!)\[[^\]]*\]\(([^)]+)\)")
IMAGE_LINK_RE = re.compile(r"!\[[^\]]*\]\(([^)]+)\)")
REFERENCE_LINK_RE = re.compile(r"^\s*\[[^\]]+\]:\s*(\S+)", re.MULTILINE)
SCHEME_RE = re.compile(r"^[A-Za-z][A-Za-z0-9+.-]*:")
IGNORED_DIR_NAMES = {".git", ".gradle", ".idea", "build"}


@dataclass(frozen=True)
class BrokenLink:
    source: Path
    target: str
    resolved: Path


def _extract_target(raw: str) -> str:
    """Return the URL/path portion of a Markdown destination.

    Common Markdown allows an optional quoted title after a whitespace-separated target.
    SudokuNova documentation does not rely on whitespace-containing unescaped local paths,
    so taking the first token makes title handling deterministic without a Markdown parser.
    Angle-bracket destinations are supported as well.
    """

    value = raw.strip()
    if value.startswith("<"):
        closing = value.find(">")
        if closing != -1:
            return value[1:closing].strip()
    return value.split(maxsplit=1)[0] if value else ""


def is_external_or_anchor(target: str) -> bool:
    if not target or target.startswith("#"):
        return True
    if target.startswith("//"):
        return True
    return bool(SCHEME_RE.match(target))


def resolve_local_target(repository_root: Path, source: Path, target: str) -> Path | None:
    target = _extract_target(target)
    if is_external_or_anchor(target):
        return None

    split = urlsplit(target)
    path_text = unquote(split.path)
    if not path_text:
        return None

    if path_text.startswith("/"):
        candidate = repository_root / path_text.lstrip("/")
    else:
        candidate = source.parent / path_text
    return candidate.resolve()


def iter_markdown_files(repository_root: Path):
    for path in repository_root.rglob("*.md"):
        relative_parts = path.relative_to(repository_root).parts
        if any(part in IGNORED_DIR_NAMES for part in relative_parts):
            continue
        if path.is_file():
            yield path


def extract_markdown_targets(text: str):
    for pattern in (INLINE_LINK_RE, IMAGE_LINK_RE, REFERENCE_LINK_RE):
        yield from pattern.findall(text)


def find_broken_links(repository_root: Path) -> list[BrokenLink]:
    repository_root = repository_root.resolve()
    broken: list[BrokenLink] = []
    for source in sorted(iter_markdown_files(repository_root)):
        try:
            text = source.read_text(encoding="utf-8")
        except UnicodeDecodeError as exc:
            raise ValueError(f"Markdown file is not valid UTF-8: {source}") from exc

        for raw_target in extract_markdown_targets(text):
            display_target = _extract_target(raw_target)
            resolved = resolve_local_target(repository_root, source, raw_target)
            if resolved is None:
                continue
            try:
                resolved.relative_to(repository_root)
            except ValueError:
                broken.append(BrokenLink(source, display_target, resolved))
                continue
            if not resolved.exists():
                broken.append(BrokenLink(source, display_target, resolved))
    return broken


def main() -> int:
    repository_root = Path(__file__).resolve().parents[1]
    try:
        broken = find_broken_links(repository_root)
    except (OSError, ValueError) as exc:
        print(f"Documentation link verification failed: {exc}", file=sys.stderr)
        return 1

    if broken:
        print("Broken local Markdown links found:", file=sys.stderr)
        for item in broken:
            source = item.source.relative_to(repository_root).as_posix()
            try:
                resolved = item.resolved.relative_to(repository_root).as_posix()
            except ValueError:
                resolved = item.resolved.as_posix()
            print(f"- {source}: {item.target!r} -> {resolved}", file=sys.stderr)
        return 1

    count = sum(1 for _ in iter_markdown_files(repository_root))
    print(f"Documentation links verified across {count} Markdown files.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
