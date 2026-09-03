#!/usr/bin/env python3
"""Regression tests for the dedicated v0.1.2 release-preparation identity."""

from __future__ import annotations

from pathlib import Path
import unittest

from scripts.verify_release_contract import (
    parse_ci_expected_contract,
    parse_desktop_package_version,
    parse_gradle_release_contract,
    parse_protected_workflow_defaults,
)


ROOT = Path(__file__).resolve().parents[2]


class V012ReleaseContractTests(unittest.TestCase):
    def test_android_source_contract(self) -> None:
        source = parse_gradle_release_contract(
            (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        )
        self.assertEqual("in.sanskar.sudokunova", source.application_id)
        self.assertEqual(2016, source.version_code)
        self.assertEqual("0.1.2", source.version_name)
        self.assertEqual(26, source.min_sdk)
        self.assertEqual(37, source.target_sdk)

    def test_desktop_package_contract(self) -> None:
        version = parse_desktop_package_version(
            (ROOT / "sharedUI" / "build.gradle.kts").read_text(encoding="utf-8")
        )
        self.assertEqual("0.1.2", version)

    def test_ordinary_ci_contract(self) -> None:
        contract = parse_ci_expected_contract(
            (ROOT / ".github" / "workflows" / "ci.yml").read_text(encoding="utf-8")
        )
        self.assertEqual("in.sanskar.sudokunova", contract.application_id)
        self.assertEqual(2016, contract.version_code)
        self.assertEqual("0.1.2", contract.version_name)
        self.assertEqual(26, contract.min_sdk)
        self.assertEqual(37, contract.target_sdk)

    def test_protected_validation_contract(self) -> None:
        contract = parse_protected_workflow_defaults(
            (ROOT / ".github" / "workflows" / "release-validation.yml").read_text(
                encoding="utf-8"
            )
        )
        self.assertEqual("in.sanskar.sudokunova", contract.application_id)
        self.assertEqual(2016, contract.version_code)
        self.assertEqual("0.1.2", contract.version_name)
        self.assertEqual(26, contract.min_sdk)
        self.assertEqual(37, contract.target_sdk)


if __name__ == "__main__":
    unittest.main()
