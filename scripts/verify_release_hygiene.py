#!/usr/bin/env python3
"""Fail CI when release/security invariants drift from SudokuNova policy."""

from __future__ import annotations

import pathlib
import re
import sys
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parents[1]
ANDROID_NS = "http://schemas.android.com/apk/res/android"
A = f"{{{ANDROID_NS}}}"


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def read(relative: str) -> str:
    path = ROOT / relative
    if not path.is_file():
        fail(f"Required file is missing: {relative}")
    return path.read_text(encoding="utf-8")


def verify_manifest() -> None:
    path = ROOT / "app/src/main/AndroidManifest.xml"
    root = ET.parse(path).getroot()
    permissions = [
        node.get(A + "name", "<unnamed>")
        for node in root.findall("uses-permission")
    ]
    if permissions:
        fail("Main manifest must remain permission-free; found: " + ", ".join(permissions))

    app = root.find("application")
    if app is None:
        fail("Main manifest has no <application> element")
    if app.get(A + "usesCleartextTraffic") != "false":
        fail('Main application must explicitly set android:usesCleartextTraffic="false"')
    if app.get(A + "dataExtractionRules") != "@xml/data_extraction_rules":
        fail("Main application must reference @xml/data_extraction_rules")
    if app.get(A + "fullBackupContent") != "@xml/backup_rules":
        fail("Main application must reference @xml/backup_rules")
    if app.get(A + "debuggable") == "true":
        fail("Main application must never be explicitly debuggable")

    exported = []
    for tag in ("activity", "activity-alias", "service", "receiver", "provider"):
        for node in app.findall(tag):
            if node.get(A + "exported") == "true":
                exported.append((tag, node.get(A + "name", "<unnamed>")))
    expected = [("activity", ".MainActivity")]
    if exported != expected:
        fail(f"Unexpected exported Android components: {exported!r}; expected {expected!r}")


def verify_modern_backup_rules() -> None:
    root = ET.parse(ROOT / "app/src/main/res/xml/data_extraction_rules.xml").getroot()
    cloud = root.find("cloud-backup")
    transfer = root.find("device-transfer")
    if cloud is None or transfer is None:
        fail("data_extraction_rules.xml must define cloud-backup and device-transfer sections")

    excluded = {(node.get("domain"), node.get("path")) for node in cloud.findall("exclude")}
    required_cloud_exclusions = {
        (domain, ".")
        for domain in (
            "root",
            "file",
            "database",
            "sharedpref",
            "external",
            "device_root",
            "device_file",
            "device_database",
            "device_sharedpref",
        )
    }
    missing = required_cloud_exclusions - excluded
    if missing:
        fail(f"Cloud backup must exclude every persisted app-data domain; missing: {sorted(missing)!r}")
    if cloud.findall("include"):
        fail("Cloud backup must not contain include rules")

    included = {(node.get("domain"), node.get("path")) for node in transfer.findall("include")}
    expected_transfer = {(domain, ".") for domain in ("file", "database", "sharedpref")}
    if included != expected_transfer:
        fail(f"Device transfer allowlist drifted: {sorted(included)!r}")


def verify_legacy_backup_rules() -> None:
    root = ET.parse(ROOT / "app/src/main/res/xml/backup_rules.xml").getroot()
    includes = root.findall("include")
    if len(includes) != 3:
        fail("Legacy backup rules must contain exactly three device-transfer-only includes")
    domains = set()
    for node in includes:
        domains.add(node.get("domain"))
        if node.get("path") != ".":
            fail("Legacy backup include paths must remain scoped to '.'")
        flags = {part.strip() for part in (node.get("requireFlags") or "").split("|") if part.strip()}
        if "deviceToDeviceTransfer" not in flags:
            fail("Every legacy backup include must require deviceToDeviceTransfer")
    if domains != {"file", "database", "sharedpref"}:
        fail(f"Unexpected legacy backup domains: {sorted(domains)!r}")


def verify_release_build() -> None:
    build = read("app/build.gradle.kts")
    required = (
        "isMinifyEnabled = true",
        "isShrinkResources = true",
        'getDefaultProguardFile("proguard-android-optimize.txt")',
        '"proguard-rules.pro"',
    )
    for token in required:
        if token not in build:
            fail(f"Release build invariant missing from app/build.gradle.kts: {token}")

    forbidden = (
        "storePassword",
        "keyPassword",
        "storeFile",
    )
    for token in forbidden:
        if token in build:
            fail(f"Signing credential/configuration token must not be hard-coded in app/build.gradle.kts: {token}")

    proguard = read("app/proguard-rules.pro")
    if "SourceFile,LineNumberTable" not in proguard:
        fail("Release rules must preserve source/line metadata for de-obfuscated crash traces")


def verify_secret_hygiene() -> None:
    gitignore = read(".gitignore")
    required_patterns = {
        "local.properties",
        "*.jks",
        "*.keystore",
        "*.p12",
        "*.pem",
        "*.key",
        "keystore.properties",
        "secrets.properties",
        ".env",
        ".env.*",
    }
    ignored = {line.strip() for line in gitignore.splitlines() if line.strip() and not line.startswith("#")}
    missing = required_patterns - ignored
    if missing:
        fail(f".gitignore is missing signing/secret exclusions: {sorted(missing)!r}")

    forbidden_names = {"local.properties", "keystore.properties", "secrets.properties", ".env"}
    forbidden_suffixes = {".jks", ".keystore", ".p12", ".pem", ".key"}
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        relative = path.relative_to(ROOT)
        if any(part in {".git", ".gradle", "build"} for part in relative.parts):
            continue
        if path.name == ".env.example":
            continue
        if path.name in forbidden_names or path.suffix.lower() in forbidden_suffixes:
            fail(f"Forbidden secret/signing file is tracked or present in the source tree: {relative}")


def verify_ci_release_gate() -> None:
    workflow = read(".github/workflows/ci.yml")
    required_tasks = (
        ":app:lintRelease",
        ":app:assembleRelease",
        ":app:bundleRelease",
    )
    for task in required_tasks:
        if task not in workflow:
            fail(f"Android CI must verify release task {task}")
    if "unsigned-release-artifacts" not in workflow:
        fail("Android CI must upload the CI-safe unsigned release artifacts")


def main() -> None:
    verify_manifest()
    verify_modern_backup_rules()
    verify_legacy_backup_rules()
    verify_release_build()
    verify_secret_hygiene()
    verify_ci_release_gate()
    print("Release hygiene verification passed.")


if __name__ == "__main__":
    main()
