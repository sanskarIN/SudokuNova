#!/usr/bin/env python3
"""Verify that source and CI release identity expectations stay synchronized.

This guard intentionally treats app/build.gradle.kts as the authoritative Android source
metadata for the current release candidate and verifies that ordinary CI plus the protected
release-validation workflow use the same package/version defaults. It does not replace
artifact metadata verification; it catches source/workflow drift before Gradle work begins.
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import sys

APPLICATION_ID_RE = re.compile(r'^\s*applicationId\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
VERSION_CODE_RE = re.compile(r'^\s*versionCode\s*=\s*(\d+)\s*$', re.MULTILINE)
VERSION_NAME_RE = re.compile(r'^\s*versionName\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
RELEASE_SAFE_VERSION_RE = re.compile(r"^[0-9A-Za-z][0-9A-Za-z._+-]*$")


@dataclass(frozen=True)
class ReleaseContract:
    application_id: str
    version_code: int
    version_name: str


def _single_match(pattern: re.Pattern[str], text: str, label: str) -> str:
    matches = pattern.findall(text)
    if len(matches) != 1:
        raise ValueError(f"Expected exactly one {label}, found {len(matches)}")
    return matches[0]


def parse_gradle_release_contract(text: str) -> ReleaseContract:
    application_id = _single_match(APPLICATION_ID_RE, text, "applicationId")
    version_code = int(_single_match(VERSION_CODE_RE, text, "versionCode"))
    version_name = _single_match(VERSION_NAME_RE, text, "versionName")

    if version_code <= 0:
        raise ValueError("versionCode must be positive")
    if not RELEASE_SAFE_VERSION_RE.fullmatch(version_name):
        raise ValueError("versionName contains unsupported release characters")
    if application_id.endswith(".debug"):
        raise ValueError("Production applicationId must not use the debug suffix")

    return ReleaseContract(application_id, version_code, version_name)


def parse_ci_expected_contract(text: str) -> ReleaseContract:
    application_id = _single_match(
        re.compile(r"--expected-application-id(?:=|\s+)([^\s\\]+)"),
        text,
        "CI expected application ID",
    )
    version_code = int(
        _single_match(
            re.compile(r"--expected-version-code(?:=|\s+)(\d+)"),
            text,
            "CI expected version code",
        )
    )
    version_name = _single_match(
        re.compile(r"--expected-version-name(?:=|\s+)([^\s\\]+)"),
        text,
        "CI expected version name",
    )
    return ReleaseContract(application_id, version_code, version_name)


def parse_protected_workflow_defaults(text: str) -> ReleaseContract:
    application_id = _single_match(
        re.compile(r"^\s*EXPECTED_APPLICATION_ID:\s*([^\s#]+)\s*$", re.MULTILINE),
        text,
        "protected-workflow expected application ID",
    )
    version_code = int(
        _single_match(
            re.compile(
                r"expected_version_code:\s*\n(?:\s+.*\n)*?\s+default:\s*['\"]?(\d+)['\"]?\s*$",
                re.MULTILINE,
            ),
            text,
            "protected-workflow default version code",
        )
    )
    version_name = _single_match(
        re.compile(
            r"expected_version_name:\s*\n(?:\s+.*\n)*?\s+default:\s*['\"]?([^'\"\s#]+)['\"]?\s*$",
            re.MULTILINE,
        ),
        text,
        "protected-workflow default version name",
    )
    return ReleaseContract(application_id, version_code, version_name)


def compare_contracts(
    source: ReleaseContract,
    ordinary_ci: ReleaseContract,
    protected_defaults: ReleaseContract,
) -> list[str]:
    errors: list[str] = []
    for label, value in (
        ("ordinary CI", ordinary_ci),
        ("protected workflow defaults", protected_defaults),
    ):
        if value.application_id != source.application_id:
            errors.append(
                f"{label} application ID {value.application_id!r} != source {source.application_id!r}"
            )
        if value.version_code != source.version_code:
            errors.append(
                f"{label} version code {value.version_code} != source {source.version_code}"
            )
        if value.version_name != source.version_name:
            errors.append(
                f"{label} version name {value.version_name!r} != source {source.version_name!r}"
            )
    return errors


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    try:
        source = parse_gradle_release_contract(
            (root / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        )
        ordinary_ci = parse_ci_expected_contract(
            (root / ".github" / "workflows" / "ci.yml").read_text(encoding="utf-8")
        )
        protected_defaults = parse_protected_workflow_defaults(
            (root / ".github" / "workflows" / "release-validation.yml").read_text(
                encoding="utf-8"
            )
        )
    except (OSError, ValueError) as exc:
        print(f"Release contract verification failed: {exc}", file=sys.stderr)
        return 1

    errors = compare_contracts(source, ordinary_ci, protected_defaults)
    if errors:
        print("Release contract drift detected:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    print(
        "Release contract verified: "
        f"applicationId={source.application_id}, "
        f"versionCode={source.version_code}, "
        f"versionName={source.version_name}."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
