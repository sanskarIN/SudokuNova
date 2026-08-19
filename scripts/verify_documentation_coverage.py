#!/usr/bin/env python3
"""Require every tracked SudokuNova file to belong to a documented repository area.

The verifier asks Git for the authoritative tracked-file set and resolves every path to a
small, ordered coverage taxonomy. A new file that does not match a rule fails closed.
Each rule also names one or more canonical documents; those documents must themselves be
tracked so a coverage rule cannot point at a missing guide.

Every tracked Markdown guide below ``docs/`` must also be discoverable from
``docs/README.md``. This keeps the documentation hub complete instead of allowing a guide
to be technically owned but effectively hidden from contributors.

Use ``--verbose`` to print the resolved documentation owner for every tracked file. Use
``--markdown`` when a Markdown per-file report is useful during an audit.
"""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path
import re
import subprocess
import sys
from typing import Iterable, Sequence


@dataclass(frozen=True)
class CoverageRule:
    """A documented repository area matched by exact paths or path prefixes."""

    name: str
    documents: tuple[str, ...]
    exact_paths: tuple[str, ...] = ()
    prefixes: tuple[str, ...] = ()

    def matches(self, path: str) -> bool:
        return path in self.exact_paths or any(path.startswith(prefix) for prefix in self.prefixes)


@dataclass(frozen=True)
class CoverageResult:
    path: str
    rule: CoverageRule


DOC_INDEX_LINK_RE = re.compile(r"(?<!!)\[[^\]]*\]\(([^)\s]+)")


COVERAGE_RULES: tuple[CoverageRule, ...] = (
    CoverageRule(
        name="Android connected and instrumentation tests",
        prefixes=("app/src/androidTest/",),
        documents=("docs/TESTING.md", "docs/QA_MATRIX.md", "docs/ACCESSIBILITY.md"),
    ),
    CoverageRule(
        name="Android JVM unit tests",
        prefixes=("app/src/test/",),
        documents=("docs/TESTING.md", "docs/PROJECT_STRUCTURE.md"),
    ),
    CoverageRule(
        name="Room schema history",
        prefixes=("app/schemas/",),
        documents=("docs/DATA_STORAGE.md", "docs/DATA_FORMATS.md", "docs/TESTING.md"),
    ),
    CoverageRule(
        name="Android localized, visual, and XML resources",
        prefixes=("app/src/main/res/",),
        documents=(
            "docs/DESIGN_SYSTEM.md",
            "docs/LOCALIZATION.md",
            "docs/ACCESSIBILITY.md",
            "docs/PROJECT_STRUCTURE.md",
        ),
    ),
    CoverageRule(
        name="Android benchmark-only application overlay",
        prefixes=("app/src/benchmark/",),
        documents=("docs/PERFORMANCE_BENCHMARKING.md", "docs/PERFORMANCE.md"),
    ),
    CoverageRule(
        name="Android application source and manifest",
        prefixes=("app/src/main/",),
        documents=(
            "docs/ARCHITECTURE.md",
            "docs/PROJECT_STRUCTURE.md",
            "docs/FEATURES.md",
            "docs/DATA_STORAGE.md",
            "docs/UI_UX.md",
        ),
    ),
    CoverageRule(
        name="Android application module configuration",
        prefixes=("app/",),
        documents=("docs/BUILDING.md", "docs/PROJECT_STRUCTURE.md", "docs/PRODUCTION_SIGNING.md"),
    ),
    CoverageRule(
        name="Sudoku engine tests",
        prefixes=("sudoku-engine/src/test/",),
        documents=("docs/SUDOKU_ENGINE.md", "docs/TESTING.md", "docs/PUZZLE_GENERATION.md"),
    ),
    CoverageRule(
        name="Sudoku engine implementation",
        prefixes=("sudoku-engine/src/main/",),
        documents=(
            "docs/SUDOKU_ENGINE.md",
            "docs/PUZZLE_GENERATION.md",
            "docs/DIFFICULTY_SYSTEM.md",
            "docs/LEARNING_AND_HINTS.md",
        ),
    ),
    CoverageRule(
        name="Sudoku engine module configuration",
        prefixes=("sudoku-engine/",),
        documents=("docs/SUDOKU_ENGINE.md", "docs/BUILDING.md", "docs/PROJECT_STRUCTURE.md"),
    ),
    CoverageRule(
        name="Macrobenchmark harness",
        prefixes=("macrobenchmark/",),
        documents=("docs/PERFORMANCE_BENCHMARKING.md", "docs/PERFORMANCE.md", "docs/TESTING.md"),
    ),
    CoverageRule(
        name="Repository guard regression tests",
        prefixes=("scripts/tests/",),
        documents=("docs/REPOSITORY_GUARDS.md", "docs/TESTING.md"),
    ),
    CoverageRule(
        name="Repository verification and maintenance scripts",
        prefixes=("scripts/",),
        documents=("docs/REPOSITORY_GUARDS.md", "docs/CI_CD.md", "docs/MAINTAINER_GUIDE.md"),
    ),
    CoverageRule(
        name="GitHub Actions workflows",
        prefixes=(".github/workflows/",),
        documents=(
            "docs/CI_CD.md",
            "docs/RELEASING.md",
            "docs/PRODUCTION_RELEASE_VALIDATION.md",
            "docs/PERFORMANCE_BENCHMARKING.md",
        ),
    ),
    CoverageRule(
        name="GitHub collaboration and repository metadata",
        prefixes=(".github/",),
        documents=(
            "docs/MAINTAINER_GUIDE.md",
            "docs/GITHUB_REPOSITORY_SETTINGS.md",
            "CONTRIBUTING.md",
        ),
    ),
    CoverageRule(
        name="Gradle wrapper and version catalog",
        prefixes=("gradle/",),
        exact_paths=("gradlew", "gradlew.bat"),
        documents=("docs/BUILDING.md", "docs/DEVELOPMENT_SETUP.md", "docs/PROJECT_STRUCTURE.md"),
    ),
    CoverageRule(
        name="Root build configuration",
        exact_paths=("build.gradle.kts", "settings.gradle.kts", "gradle.properties"),
        documents=("docs/BUILDING.md", "docs/DEVELOPMENT_SETUP.md", "docs/PROJECT_STRUCTURE.md"),
    ),
    CoverageRule(
        name="Repository editor and ignore configuration",
        exact_paths=(".editorconfig", ".gitignore"),
        documents=("docs/CONTRIBUTING_GUIDE.md", "docs/PROJECT_STRUCTURE.md"),
    ),
    CoverageRule(
        name="Root project, policy, and community documents",
        exact_paths=(
            "AUTHORS.md",
            "CHANGELOG.md",
            "CODE_OF_CONDUCT.md",
            "CONTRIBUTING.md",
            "LICENSE",
            "README.md",
            "ROADMAP.md",
            "SECURITY.md",
            "SUPPORT.md",
            "THIRD_PARTY_NOTICES.md",
            "what_changed.md",
        ),
        documents=("docs/README.md", "docs/DOCUMENTATION_STANDARDS.md"),
    ),
    CoverageRule(
        name="Detailed documentation library",
        prefixes=("docs/",),
        documents=("docs/README.md", "docs/DOCUMENTATION_STANDARDS.md"),
    ),
)


def coverage_for_path(path: str) -> CoverageRule | None:
    """Return the first, most-specific coverage rule for ``path``."""

    normalized = path.replace("\\", "/")
    if normalized.startswith("./"):
        normalized = normalized[2:]
    for rule in COVERAGE_RULES:
        if rule.matches(normalized):
            return rule
    return None


def list_tracked_files(repository_root: Path) -> list[str]:
    """Return Git's authoritative tracked-file set using NUL-safe output."""

    completed = subprocess.run(
        ["git", "ls-files", "-z"],
        cwd=repository_root,
        check=True,
        capture_output=True,
    )
    return sorted(
        path.decode("utf-8")
        for path in completed.stdout.split(b"\0")
        if path
    )


def validate_coverage(paths: Iterable[str], tracked_paths: Iterable[str]) -> tuple[list[CoverageResult], list[str]]:
    """Resolve all paths and return coverage results plus human-readable errors."""

    tracked = set(tracked_paths)
    results: list[CoverageResult] = []
    errors: list[str] = []

    for path in sorted(set(paths)):
        rule = coverage_for_path(path)
        if rule is None:
            errors.append(f"Tracked file has no documentation coverage rule: {path}")
            continue
        missing_documents = [document for document in rule.documents if document not in tracked]
        if missing_documents:
            errors.append(
                f"Coverage rule {rule.name!r} for {path} references untracked/missing document(s): "
                + ", ".join(missing_documents)
            )
            continue
        results.append(CoverageResult(path=path, rule=rule))

    return results, errors


def validate_documentation_index(tracked_paths: Iterable[str], index_text: str) -> list[str]:
    """Require every tracked detailed Markdown guide to be linked by ``docs/README.md``."""

    linked_targets = {
        target.split("#", maxsplit=1)[0].removeprefix("./")
        for target in DOC_INDEX_LINK_RE.findall(index_text)
    }
    detailed_guides = sorted(
        path.removeprefix("docs/")
        for path in set(tracked_paths)
        if path.startswith("docs/") and path.endswith(".md") and path != "docs/README.md"
    )
    return [
        f"Detailed documentation file is not linked from docs/README.md: docs/{guide}"
        for guide in detailed_guides
        if guide not in linked_targets
    ]


def render_markdown(results: Sequence[CoverageResult]) -> str:
    """Render a deterministic per-file audit table."""

    lines = [
        "| Tracked file | Documentation area | Canonical documentation |",
        "|---|---|---|",
    ]
    for result in results:
        documents = "<br>".join(f"`{document}`" for document in result.rule.documents)
        lines.append(f"| `{result.path}` | {result.rule.name} | {documents} |")
    return "\n".join(lines)


def parse_args(argv: Sequence[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    output = parser.add_mutually_exclusive_group()
    output.add_argument("--verbose", action="store_true", help="print one coverage line per tracked file")
    output.add_argument("--markdown", action="store_true", help="print a Markdown per-file coverage table")
    return parser.parse_args(argv)


def main(argv: Sequence[str] | None = None) -> int:
    args = parse_args(argv)
    repository_root = Path(__file__).resolve().parents[1]

    try:
        tracked = list_tracked_files(repository_root)
        index_text = (repository_root / "docs" / "README.md").read_text(encoding="utf-8")
    except (OSError, subprocess.CalledProcessError, UnicodeDecodeError) as exc:
        print(f"Documentation coverage verification failed while reading repository state: {exc}", file=sys.stderr)
        return 1

    results, errors = validate_coverage(tracked, tracked)
    errors.extend(validate_documentation_index(tracked, index_text))
    if errors:
        print("Documentation coverage verification failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    if args.markdown:
        print(render_markdown(results))
    elif args.verbose:
        for result in results:
            documents = ", ".join(result.rule.documents)
            print(f"[{result.rule.name}] {result.path} -> {documents}")

    area_count = len({result.rule.name for result in results})
    detailed_guide_count = sum(
        1
        for path in tracked
        if path.startswith("docs/") and path.endswith(".md") and path != "docs/README.md"
    )
    print(
        f"Documentation coverage verified for {len(results)} tracked files across {area_count} areas; "
        f"{detailed_guide_count} detailed guides are indexed."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
