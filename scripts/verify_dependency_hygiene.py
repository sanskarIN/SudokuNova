#!/usr/bin/env python3
"""Verify deterministic dependency declarations and notice coverage."""

from __future__ import annotations

import pathlib
import re
import sys
import tomllib

ROOT = pathlib.Path(__file__).resolve().parents[1]
CATALOG = ROOT / "gradle/libs.versions.toml"
NOTICES = ROOT / "THIRD_PARTY_NOTICES.md"

DYNAMIC_PATTERNS = (
    re.compile(r"(^|[.\-])latest([.\-]|$)", re.IGNORECASE),
    re.compile(r"snapshot", re.IGNORECASE),
    re.compile(r"\+"),
    re.compile(r"^[\[(]"),
)


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def is_dynamic(version: str) -> bool:
    return any(pattern.search(version) for pattern in DYNAMIC_PATTERNS)


def main() -> None:
    if not CATALOG.is_file():
        fail("gradle/libs.versions.toml is missing")
    if not NOTICES.is_file():
        fail("THIRD_PARTY_NOTICES.md is missing")

    catalog = tomllib.loads(CATALOG.read_text(encoding="utf-8"))
    versions: dict[str, str] = catalog.get("versions", {})
    libraries: dict[str, dict[str, object]] = catalog.get("libraries", {})
    plugins: dict[str, dict[str, object]] = catalog.get("plugins", {})

    if not versions or not libraries or not plugins:
        fail("Version catalog must contain versions, libraries, and plugins")

    for alias, version in versions.items():
        if not isinstance(version, str) or not version.strip():
            fail(f"Version alias {alias!r} must be a non-empty string")
        if is_dynamic(version):
            fail(f"Dynamic/non-reproducible version is forbidden: {alias}={version}")

    for alias, library in libraries.items():
        module = library.get("module")
        if not isinstance(module, str) or module.count(":") != 1:
            fail(f"Library {alias!r} must use an exact group:artifact module coordinate")

        version_ref = library.get("version.ref")
        inline_version = library.get("version")
        if version_ref is not None:
            if not isinstance(version_ref, str) or version_ref not in versions:
                fail(f"Library {alias!r} references unknown version alias {version_ref!r}")
        elif inline_version is not None:
            if not isinstance(inline_version, str) or is_dynamic(inline_version):
                fail(f"Library {alias!r} has an invalid inline version {inline_version!r}")
        elif not module.startswith("androidx.compose"):
            fail(
                f"Unversioned library {alias!r} ({module}) is not a Compose BOM-managed module",
            )

    for alias, plugin in plugins.items():
        plugin_id = plugin.get("id")
        version_ref = plugin.get("version.ref")
        inline_version = plugin.get("version")
        if not isinstance(plugin_id, str) or not plugin_id.strip():
            fail(f"Plugin {alias!r} must declare a non-empty id")
        if version_ref is not None:
            if not isinstance(version_ref, str) or version_ref not in versions:
                fail(f"Plugin {alias!r} references unknown version alias {version_ref!r}")
        elif inline_version is not None:
            if not isinstance(inline_version, str) or is_dynamic(inline_version):
                fail(f"Plugin {alias!r} has an invalid inline version {inline_version!r}")
        else:
            fail(f"Plugin {alias!r} must declare an explicit version or version.ref")

    notices = NOTICES.read_text(encoding="utf-8").lower()
    required_notice_terms = {
        "androidx": "AndroidX/Jetpack notice",
        "kotlin": "Kotlin notice",
        "gradle": "Gradle notice",
        "junit": "JUnit notice",
        "apache license 2.0": "Apache-2.0 notice",
        "eclipse public license 1.0": "JUnit EPL-1.0 notice",
    }
    for term, description in required_notice_terms.items():
        if term not in notices:
            fail(f"THIRD_PARTY_NOTICES.md is missing required {description}: {term!r}")

    print(
        "Dependency hygiene verification passed: "
        f"{len(versions)} versions, {len(libraries)} libraries, {len(plugins)} plugins.",
    )


if __name__ == "__main__":
    main()
