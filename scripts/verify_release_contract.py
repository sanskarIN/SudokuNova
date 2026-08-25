#!/usr/bin/env python3
"""Verify that source and CI release identity expectations stay synchronized.

The Android app metadata is authoritative for the current release identity. This guard
verifies that ordinary CI, protected release-validation defaults, and the shared Desktop
package version stay aligned with it. It does not replace artifact or embedded-manifest
verification; it catches source/workflow/package drift before expensive build work begins.
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import sys

APPLICATION_ID_RE = re.compile(r'^\s*applicationId\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
MIN_SDK_RE = re.compile(r"^\s*minSdk\s*=\s*(\d+)\s*$", re.MULTILINE)
TARGET_SDK_RE = re.compile(r"^\s*targetSdk\s*=\s*(\d+)\s*$", re.MULTILINE)
VERSION_CODE_RE = re.compile(r'^\s*versionCode\s*=\s*(\d+)\s*$', re.MULTILINE)
VERSION_NAME_RE = re.compile(r'^\s*versionName\s*=\s*"([^"]+)"\s*$', re.MULTILINE)
DESKTOP_PACKAGE_VERSION_RE = re.compile(
    r'^\s*packageVersion\s*=\s*"([^"]+)"\s*$',
    re.MULTILINE,
)
RELEASE_SAFE_VERSION_RE = re.compile(r"^[0-9A-Za-z][0-9A-Za-z._+-]*$")


@dataclass(frozen=True)
class ReleaseContract:
    application_id: str
    version_code: int
    version_name: str
    min_sdk: int
    target_sdk: int


def _single_match(pattern: re.Pattern[str], text: str, label: str) -> str:
    matches = pattern.findall(text)
    if len(matches) != 1:
        raise ValueError(f"Expected exactly one {label}, found {len(matches)}")
    return matches[0]


def _positive_int(value: str, label: str) -> int:
    parsed = int(value)
    if parsed <= 0:
        raise ValueError(f"{label} must be positive")
    return parsed


def _validate_release_version(value: str, label: str) -> str:
    if not RELEASE_SAFE_VERSION_RE.fullmatch(value):
        raise ValueError(f"{label} contains unsupported release characters")
    return value


def parse_gradle_release_contract(text: str) -> ReleaseContract:
    application_id = _single_match(APPLICATION_ID_RE, text, "applicationId")
    min_sdk = _positive_int(_single_match(MIN_SDK_RE, text, "minSdk"), "minSdk")
    target_sdk = _positive_int(_single_match(TARGET_SDK_RE, text, "targetSdk"), "targetSdk")
    version_code = _positive_int(
        _single_match(VERSION_CODE_RE, text, "versionCode"),
        "versionCode",
    )
    version_name = _validate_release_version(
        _single_match(VERSION_NAME_RE, text, "versionName"),
        "versionName",
    )

    if application_id.endswith(".debug"):
        raise ValueError("Production applicationId must not use the debug suffix")
    if target_sdk < min_sdk:
        raise ValueError("targetSdk must be greater than or equal to minSdk")

    return ReleaseContract(application_id, version_code, version_name, min_sdk, target_sdk)


def parse_desktop_package_version(text: str) -> str:
    return _validate_release_version(
        _single_match(DESKTOP_PACKAGE_VERSION_RE, text, "Desktop packageVersion"),
        "Desktop packageVersion",
    )


def parse_ci_expected_contract(text: str) -> ReleaseContract:
    application_id = _single_match(
        re.compile(r"--expected-application-id(?:=|\s+)([^\s\\]+)"),
        text,
        "CI expected application ID",
    )
    version_code = _positive_int(
        _single_match(
            re.compile(r"--expected-version-code(?:=|\s+)(\d+)"),
            text,
            "CI expected version code",
        ),
        "CI expected version code",
    )
    version_name = _single_match(
        re.compile(r"--expected-version-name(?:=|\s+)([^\s\\]+)"),
        text,
        "CI expected version name",
    )
    min_sdk = _positive_int(
        _single_match(
            re.compile(r"--expected-min-sdk(?:=|\s+)(\d+)"),
            text,
            "CI expected min SDK",
        ),
        "CI expected min SDK",
    )
    target_sdk = _positive_int(
        _single_match(
            re.compile(r"--expected-target-sdk(?:=|\s+)(\d+)"),
            text,
            "CI expected target SDK",
        ),
        "CI expected target SDK",
    )
    return ReleaseContract(application_id, version_code, version_name, min_sdk, target_sdk)


def parse_protected_workflow_defaults(text: str) -> ReleaseContract:
    application_id = _single_match(
        re.compile(r"^\s*EXPECTED_APPLICATION_ID:\s*([^\s#]+)\s*$", re.MULTILINE),
        text,
        "protected-workflow expected application ID",
    )
    min_sdk = _positive_int(
        _single_match(
            re.compile(r"^\s*EXPECTED_MIN_SDK:\s*['\"]?(\d+)['\"]?\s*$", re.MULTILINE),
            text,
            "protected-workflow expected min SDK",
        ),
        "protected-workflow expected min SDK",
    )
    target_sdk = _positive_int(
        _single_match(
            re.compile(r"^\s*EXPECTED_TARGET_SDK:\s*['\"]?(\d+)['\"]?\s*$", re.MULTILINE),
            text,
            "protected-workflow expected target SDK",
        ),
        "protected-workflow expected target SDK",
    )
    version_code = _positive_int(
        _single_match(
            re.compile(
                r"expected_version_code:\s*\n(?:\s+.*\n)*?\s+default:\s*['\"]?(\d+)['\"]?\s*$",
                re.MULTILINE,
            ),
            text,
            "protected-workflow default version code",
        ),
        "protected-workflow default version code",
    )
    version_name = _single_match(
        re.compile(
            r"expected_version_name:\s*\n(?:\s+.*\n)*?\s+default:\s*['\"]?([^'\"\s#]+)['\"]?\s*$",
            re.MULTILINE,
        ),
        text,
        "protected-workflow default version name",
    )
    return ReleaseContract(application_id, version_code, version_name, min_sdk, target_sdk)


def compare_contracts(
    source: ReleaseContract,
    ordinary_ci: ReleaseContract,
    protected_defaults: ReleaseContract,
    desktop_package_version: str | None = None,
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
        if value.min_sdk != source.min_sdk:
            errors.append(f"{label} min SDK {value.min_sdk} != source {source.min_sdk}")
        if value.target_sdk != source.target_sdk:
            errors.append(f"{label} target SDK {value.target_sdk} != source {source.target_sdk}")

    if desktop_package_version is not None and desktop_package_version != source.version_name:
        errors.append(
            f"Desktop package version {desktop_package_version!r} != source version name {source.version_name!r}"
        )
    return errors


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    try:
        source = parse_gradle_release_contract(
            (root / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        )
        desktop_package_version = parse_desktop_package_version(
            (root / "sharedUI" / "build.gradle.kts").read_text(encoding="utf-8")
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

    errors = compare_contracts(
        source,
        ordinary_ci,
        protected_defaults,
        desktop_package_version=desktop_package_version,
    )
    if errors:
        print("Release contract drift detected:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    print(
        "Release contract verified: "
        f"applicationId={source.application_id}, "
        f"versionCode={source.version_code}, "
        f"versionName={source.version_name}, "
        f"desktopPackageVersion={desktop_package_version}, "
        f"minSdk={source.min_sdk}, targetSdk={source.target_sdk}."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
