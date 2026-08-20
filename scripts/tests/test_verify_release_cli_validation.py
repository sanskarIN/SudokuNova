import unittest

from scripts.verify_release_outputs import main


class ReleaseVerifierCliValidationTest(unittest.TestCase):
    def base_args(self) -> list[str]:
        return [
            "--apk",
            "missing.apk",
            "--aab",
            "missing.aab",
            "--mapping",
            "missing-mapping.txt",
            "--metadata",
            "missing-output-metadata.json",
            "--expected-version-code",
            "1000",
            "--expected-version-name",
            "1.0.0-rc.1",
            "--expected-application-id",
            "in.sanskar.sudokunova",
            "--output",
            "missing-evidence.txt",
        ]

    def test_rejects_zero_expected_min_sdk_before_artifact_access(self) -> None:
        args = self.base_args() + [
            "--require-apk-manifest",
            "--expected-min-sdk",
            "0",
        ]

        self.assertEqual(1, main(args))

    def test_rejects_target_sdk_below_min_sdk_before_artifact_access(self) -> None:
        args = self.base_args() + [
            "--require-apk-manifest",
            "--expected-min-sdk",
            "37",
            "--expected-target-sdk",
            "26",
        ]

        self.assertEqual(1, main(args))

    def test_rejects_identity_output_without_manifest_verification(self) -> None:
        args = self.base_args() + [
            "--apk-identity-output",
            "apk-identity.txt",
        ]

        self.assertEqual(1, main(args))


if __name__ == "__main__":
    unittest.main()
