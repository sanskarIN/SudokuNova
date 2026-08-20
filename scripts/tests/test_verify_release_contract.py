import unittest

from scripts.verify_release_contract import (
    ReleaseContract,
    compare_contracts,
    parse_ci_expected_contract,
    parse_gradle_release_contract,
    parse_protected_workflow_defaults,
)


class ReleaseContractVerifierTest(unittest.TestCase):
    def test_parses_gradle_contract(self) -> None:
        contract = parse_gradle_release_contract(
            '''
            android {
                defaultConfig {
                    applicationId = "in.sanskar.sudokunova"
                    minSdk = 26
                    targetSdk = 37
                    versionCode = 1000
                    versionName = "1.0.0-rc.1"
                }
            }
            '''
        )

        self.assertEqual(
            ReleaseContract("in.sanskar.sudokunova", 1000, "1.0.0-rc.1", 26, 37),
            contract,
        )

    def test_rejects_duplicate_gradle_identity(self) -> None:
        with self.assertRaisesRegex(ValueError, "exactly one applicationId"):
            parse_gradle_release_contract(
                '''
                applicationId = "in.sanskar.sudokunova"
                applicationId = "in.sanskar.other"
                minSdk = 26
                targetSdk = 37
                versionCode = 1000
                versionName = "1.0.0-rc.1"
                '''
            )

    def test_rejects_debug_production_application_id(self) -> None:
        with self.assertRaisesRegex(ValueError, "debug suffix"):
            parse_gradle_release_contract(
                '''
                applicationId = "in.sanskar.sudokunova.debug"
                minSdk = 26
                targetSdk = 37
                versionCode = 1000
                versionName = "1.0.0-rc.1"
                '''
            )

    def test_rejects_unsafe_version_name(self) -> None:
        with self.assertRaisesRegex(ValueError, "unsupported release characters"):
            parse_gradle_release_contract(
                '''
                applicationId = "in.sanskar.sudokunova"
                minSdk = 26
                targetSdk = 37
                versionCode = 1000
                versionName = "1.0.0 rc 1"
                '''
            )

    def test_rejects_target_sdk_below_min_sdk(self) -> None:
        with self.assertRaisesRegex(ValueError, "targetSdk must be greater"):
            parse_gradle_release_contract(
                '''
                applicationId = "in.sanskar.sudokunova"
                minSdk = 37
                targetSdk = 26
                versionCode = 1000
                versionName = "1.0.0-rc.1"
                '''
            )

    def test_parses_ordinary_ci_expected_contract(self) -> None:
        contract = parse_ci_expected_contract(
            '''
            python scripts/verify_release_outputs.py \\
              --expected-version-code 1000 \\
              --expected-version-name 1.0.0-rc.1 \\
              --expected-application-id in.sanskar.sudokunova \\
              --expected-min-sdk 26 \\
              --expected-target-sdk 37
            '''
        )

        self.assertEqual(
            ReleaseContract("in.sanskar.sudokunova", 1000, "1.0.0-rc.1", 26, 37),
            contract,
        )

    def test_parses_protected_workflow_defaults(self) -> None:
        contract = parse_protected_workflow_defaults(
            '''
            on:
              workflow_dispatch:
                inputs:
                  expected_version_code:
                    description: Expected code
                    required: true
                    type: string
                    default: '1000'
                  expected_version_name:
                    description: Expected name
                    required: true
                    type: string
                    default: '1.0.0-rc.1'
            jobs:
              validate:
                env:
                  EXPECTED_APPLICATION_ID: in.sanskar.sudokunova
                  EXPECTED_MIN_SDK: '26'
                  EXPECTED_TARGET_SDK: '37'
            '''
        )

        self.assertEqual(
            ReleaseContract("in.sanskar.sudokunova", 1000, "1.0.0-rc.1", 26, 37),
            contract,
        )

    def test_compare_contracts_accepts_matching_values(self) -> None:
        source = ReleaseContract("in.sanskar.sudokunova", 1000, "1.0.0-rc.1", 26, 37)

        self.assertEqual([], compare_contracts(source, source, source))

    def test_compare_contracts_reports_every_drift(self) -> None:
        source = ReleaseContract("in.sanskar.sudokunova", 1000, "1.0.0-rc.1", 26, 37)
        ordinary = ReleaseContract("in.sanskar.other", 999, "wrong", 25, 36)
        protected = ReleaseContract("in.sanskar.other2", 998, "wrong2", 24, 35)

        errors = compare_contracts(source, ordinary, protected)

        self.assertEqual(10, len(errors))
        self.assertTrue(any("ordinary CI application ID" in error for error in errors))
        self.assertTrue(any("ordinary CI min SDK" in error for error in errors))
        self.assertTrue(any("protected workflow defaults target SDK" in error for error in errors))
        self.assertTrue(any("protected workflow defaults version name" in error for error in errors))


if __name__ == "__main__":
    unittest.main()
