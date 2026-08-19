import json
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest import mock
import zipfile

from scripts.verify_release_outputs import (
    AAB_REQUIRED_ENTRIES,
    APK_REQUIRED_ENTRIES,
    ApkManifestIdentity,
    inspect_apk_manifest,
    load_apk_application_id,
    load_apk_metadata,
    main,
    normalize_cert_sha256,
    parse_apksigner_cert_sha256,
    parse_apksigner_verified_schemes,
    parse_keytool_cert_sha256,
    validate_zip,
    verify_aab_signature,
    verify_apk_manifest,
    verify_apk_signature,
    write_apk_identity_evidence,
    write_manifest,
    write_signature_evidence,
)

TEST_DIGEST = "11" * 32
OTHER_DIGEST = "22" * 32
COLON_DIGEST = ":".join(TEST_DIGEST[i : i + 2] for i in range(0, len(TEST_DIGEST), 2)).upper()
APPLICATION_ID = "in.sanskar.sudokunova"


class ReleaseOutputVerifierTest(unittest.TestCase):
    def make_zip(self, path: Path, entries: set[str]) -> None:
        with zipfile.ZipFile(path, "w") as archive:
            for entry in sorted(entries):
                archive.writestr(entry, b"test")

    def write_metadata(
        self,
        path: Path,
        *,
        application_id: str | None = APPLICATION_ID,
        version_code: int = 1000,
        version_name: str = "1.0.0-rc.1",
    ) -> None:
        payload: dict[str, object] = {
            "elements": [
                {
                    "versionCode": version_code,
                    "versionName": version_name,
                }
            ]
        }
        if application_id is not None:
            payload["applicationId"] = application_id
        path.write_text(json.dumps(payload), encoding="utf-8")

    def apksigner_output(self, *, include_v2: bool = True) -> str:
        lines = [
            "Verified using v1 scheme (JAR signing): true",
            f"Verified using v2 scheme (APK Signature Scheme v2): {'true' if include_v2 else 'false'}",
            "Verified using v3 scheme (APK Signature Scheme v3): false",
            f"Signer #1 certificate SHA-256 digest: {COLON_DIGEST}",
        ]
        return "\n".join(lines) + "\n"

    def test_accepts_minimal_valid_apk_and_aab_structures(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            apk = root / "app-release-unsigned.apk"
            aab = root / "app-release.aab"
            self.make_zip(apk, APK_REQUIRED_ENTRIES)
            self.make_zip(aab, AAB_REQUIRED_ENTRIES)

            validate_zip(apk, "Release APK", APK_REQUIRED_ENTRIES)
            validate_zip(aab, "Release AAB", AAB_REQUIRED_ENTRIES)

    def test_rejects_archive_missing_required_entry(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "broken.apk"
            self.make_zip(apk, {"AndroidManifest.xml"})

            with self.assertRaisesRegex(ValueError, "missing required entries"):
                validate_zip(apk, "Release APK", APK_REQUIRED_ENTRIES)

    def test_reads_single_apk_metadata_element(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            metadata = Path(temp_dir) / "output-metadata.json"
            self.write_metadata(metadata)
            self.assertEqual((1000, "1.0.0-rc.1"), load_apk_metadata(metadata))

    def test_reads_apk_application_id(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            metadata = Path(temp_dir) / "output-metadata.json"
            self.write_metadata(metadata)
            self.assertEqual(APPLICATION_ID, load_apk_application_id(metadata))

    def test_rejects_missing_apk_application_id(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            metadata = Path(temp_dir) / "output-metadata.json"
            self.write_metadata(metadata, application_id=None)
            with self.assertRaisesRegex(ValueError, "applicationId"):
                load_apk_application_id(metadata)

    def test_rejects_multiple_apk_metadata_elements(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            metadata = Path(temp_dir) / "output-metadata.json"
            metadata.write_text(
                json.dumps(
                    {
                        "applicationId": APPLICATION_ID,
                        "elements": [
                            {"versionCode": 1000, "versionName": "1.0.0-rc.1"},
                            {"versionCode": 1001, "versionName": "1.0.0"},
                        ],
                    }
                ),
                encoding="utf-8",
            )

            with self.assertRaisesRegex(ValueError, "exactly one release element"):
                load_apk_metadata(metadata)

    def test_main_rejects_wrong_application_id(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            apk = root / "app-release-unsigned.apk"
            aab = root / "app-release.aab"
            mapping = root / "mapping.txt"
            metadata = root / "output-metadata.json"
            output = root / "sha256.txt"
            self.make_zip(apk, APK_REQUIRED_ENTRIES)
            self.make_zip(aab, AAB_REQUIRED_ENTRIES)
            mapping.write_text("mapping", encoding="utf-8")
            self.write_metadata(metadata, application_id="example.wrong.application")

            exit_code = main(
                [
                    "--apk",
                    str(apk),
                    "--aab",
                    str(aab),
                    "--mapping",
                    str(mapping),
                    "--metadata",
                    str(metadata),
                    "--expected-version-code",
                    "1000",
                    "--expected-version-name",
                    "1.0.0-rc.1",
                    "--expected-application-id",
                    APPLICATION_ID,
                    "--output",
                    str(output),
                ]
            )

            self.assertEqual(1, exit_code)
            self.assertFalse(output.exists())

    def test_main_rejects_apk_sdk_expectations_without_manifest_verification(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            apk = root / "app-release-unsigned.apk"
            aab = root / "app-release.aab"
            mapping = root / "mapping.txt"
            metadata = root / "output-metadata.json"
            output = root / "sha256.txt"
            self.make_zip(apk, APK_REQUIRED_ENTRIES)
            self.make_zip(aab, AAB_REQUIRED_ENTRIES)
            mapping.write_text("mapping", encoding="utf-8")
            self.write_metadata(metadata)

            exit_code = main(
                [
                    "--apk",
                    str(apk),
                    "--aab",
                    str(aab),
                    "--mapping",
                    str(mapping),
                    "--metadata",
                    str(metadata),
                    "--expected-version-code",
                    "1000",
                    "--expected-version-name",
                    "1.0.0-rc.1",
                    "--expected-application-id",
                    APPLICATION_ID,
                    "--expected-min-sdk",
                    "26",
                    "--output",
                    str(output),
                ]
            )

            self.assertEqual(1, exit_code)
            self.assertFalse(output.exists())

    def test_manifest_is_stable_and_contains_hash_size_and_path(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            first = root / "a.bin"
            second = root / "b.bin"
            output = root / "checksums.txt"
            first.write_bytes(b"a")
            second.write_bytes(b"bb")

            write_manifest([second, first], output)
            text = output.read_text(encoding="utf-8")

            self.assertIn("size_bytes", text)
            self.assertIn("a.bin", text)
            self.assertIn("b.bin", text)
            self.assertEqual(4, len(text.strip().splitlines()))

    def test_inspects_embedded_apk_manifest_values(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"apk-placeholder")
            results = [
                subprocess.CompletedProcess(["apkanalyzer"], 0, APPLICATION_ID + "\n", ""),
                subprocess.CompletedProcess(["apkanalyzer"], 0, "1000\n", ""),
                subprocess.CompletedProcess(["apkanalyzer"], 0, "1.0.0-rc.1\n", ""),
                subprocess.CompletedProcess(["apkanalyzer"], 0, "26\n", ""),
                subprocess.CompletedProcess(["apkanalyzer"], 0, "37\n", ""),
                subprocess.CompletedProcess(["apkanalyzer"], 0, "false\n", ""),
            ]
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/sdk/apkanalyzer"),
                mock.patch("scripts.verify_release_outputs._run_tool", side_effect=results),
            ):
                self.assertEqual(
                    ApkManifestIdentity(APPLICATION_ID, 1000, "1.0.0-rc.1", 26, 37, False),
                    inspect_apk_manifest(apk),
                )

    def test_manifest_verification_rejects_debuggable_release(self) -> None:
        identity = ApkManifestIdentity(APPLICATION_ID, 1000, "1.0.0-rc.1", 26, 37, True)
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"apk-placeholder")
            with mock.patch("scripts.verify_release_outputs.inspect_apk_manifest", return_value=identity):
                with self.assertRaisesRegex(RuntimeError, "debuggable"):
                    verify_apk_manifest(
                        apk,
                        expected_application_id=APPLICATION_ID,
                        expected_version_code=1000,
                        expected_version_name="1.0.0-rc.1",
                        expected_min_sdk=26,
                        expected_target_sdk=37,
                    )

    def test_manifest_verification_rejects_target_sdk_drift(self) -> None:
        identity = ApkManifestIdentity(APPLICATION_ID, 1000, "1.0.0-rc.1", 26, 36, False)
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"apk-placeholder")
            with mock.patch("scripts.verify_release_outputs.inspect_apk_manifest", return_value=identity):
                with self.assertRaisesRegex(RuntimeError, "targetSdk mismatch"):
                    verify_apk_manifest(
                        apk,
                        expected_application_id=APPLICATION_ID,
                        expected_version_code=1000,
                        expected_version_name="1.0.0-rc.1",
                        expected_min_sdk=26,
                        expected_target_sdk=37,
                    )

    def test_apk_identity_evidence_is_stable(self) -> None:
        identity = ApkManifestIdentity(APPLICATION_ID, 1000, "1.0.0-rc.1", 26, 37, False)
        with tempfile.TemporaryDirectory() as temp_dir:
            output = Path(temp_dir) / "apk-identity.txt"
            write_apk_identity_evidence(output, identity)
            self.assertEqual(
                "# SudokuNova embedded APK manifest identity evidence\n"
                f"application_id={APPLICATION_ID}\n"
                "version_code=1000\n"
                "version_name=1.0.0-rc.1\n"
                "min_sdk=26\n"
                "target_sdk=37\n"
                "debuggable=false\n",
                output.read_text(encoding="utf-8"),
            )

    def test_normalizes_colon_separated_certificate_fingerprint(self) -> None:
        self.assertEqual(TEST_DIGEST, normalize_cert_sha256(COLON_DIGEST))

    def test_rejects_invalid_certificate_fingerprint(self) -> None:
        with self.assertRaisesRegex(ValueError, "64 hexadecimal"):
            normalize_cert_sha256("not-a-fingerprint")

    def test_parses_apksigner_certificate_digest(self) -> None:
        output = f"Signer #1 certificate SHA-256 digest: {COLON_DIGEST}\n"
        self.assertEqual([TEST_DIGEST], parse_apksigner_cert_sha256(output))

    def test_parses_apksigner_verified_signature_schemes(self) -> None:
        self.assertEqual([1, 2], parse_apksigner_verified_schemes(self.apksigner_output()))

    def test_parses_keytool_certificate_digest(self) -> None:
        output = f"Certificate fingerprints:\n\t SHA256: {COLON_DIGEST}\n"
        self.assertEqual([TEST_DIGEST], parse_keytool_cert_sha256(output))

    def test_apk_signature_requirement_fails_without_apksigner(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"signed-artifact-placeholder")
            with mock.patch("scripts.verify_release_outputs.shutil.which", return_value=None):
                with self.assertRaisesRegex(RuntimeError, "apksigner is required"):
                    verify_apk_signature(apk)

    def test_apk_signature_accepts_expected_certificate(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"signed-artifact-placeholder")
            completed = subprocess.CompletedProcess(
                args=["apksigner"],
                returncode=0,
                stdout=self.apksigner_output(),
                stderr="",
            )
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/sdk/apksigner"),
                mock.patch("scripts.verify_release_outputs._run_tool", return_value=completed),
            ):
                self.assertEqual([TEST_DIGEST], verify_apk_signature(apk, TEST_DIGEST))

    def test_apk_signature_rejects_v1_only_signature(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"signed-artifact-placeholder")
            completed = subprocess.CompletedProcess(
                args=["apksigner"],
                returncode=0,
                stdout=self.apksigner_output(include_v2=False),
                stderr="",
            )
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/sdk/apksigner"),
                mock.patch("scripts.verify_release_outputs._run_tool", return_value=completed),
            ):
                with self.assertRaisesRegex(RuntimeError, "v2-or-newer"):
                    verify_apk_signature(apk, TEST_DIGEST)

    def test_apk_signature_rejects_unexpected_certificate(self) -> None:
        completed = subprocess.CompletedProcess(
            args=["apksigner"],
            returncode=0,
            stdout=self.apksigner_output(),
            stderr="",
        )
        with tempfile.TemporaryDirectory() as temp_dir:
            apk = Path(temp_dir) / "app-release.apk"
            apk.write_bytes(b"signed-artifact-placeholder")
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/sdk/apksigner"),
                mock.patch("scripts.verify_release_outputs._run_tool", return_value=completed),
            ):
                with self.assertRaisesRegex(RuntimeError, "APK signer certificate SHA-256 mismatch"):
                    verify_apk_signature(apk, OTHER_DIGEST)

    def test_aab_signature_requirement_fails_without_jarsigner(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            aab = Path(temp_dir) / "app-release.aab"
            aab.write_bytes(b"signed-artifact-placeholder")
            with mock.patch("scripts.verify_release_outputs.shutil.which", return_value=None):
                with self.assertRaisesRegex(RuntimeError, "jarsigner is required"):
                    verify_aab_signature(aab)

    def test_unsigned_aab_is_rejected_even_when_jarsigner_returns_zero(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            aab = Path(temp_dir) / "app-release.aab"
            aab.write_bytes(b"signed-artifact-placeholder")
            completed = subprocess.CompletedProcess(
                args=["jarsigner"],
                returncode=0,
                stdout="jar is unsigned.\n",
                stderr="",
            )
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/usr/bin/jarsigner"),
                mock.patch("scripts.verify_release_outputs._run_tool", return_value=completed),
            ):
                with self.assertRaisesRegex(RuntimeError, "AAB signature verification failed"):
                    verify_aab_signature(aab)

    def test_verified_aab_records_expected_certificate(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            aab = Path(temp_dir) / "app-release.aab"
            aab.write_bytes(b"signed-artifact-placeholder")
            verify_result = subprocess.CompletedProcess(
                args=["jarsigner"],
                returncode=0,
                stdout="jar verified.\n",
                stderr="",
            )
            cert_result = subprocess.CompletedProcess(
                args=["keytool"],
                returncode=0,
                stdout=f"Certificate fingerprints:\n\t SHA256: {COLON_DIGEST}\n",
                stderr="",
            )
            with (
                mock.patch(
                    "scripts.verify_release_outputs.shutil.which",
                    side_effect=lambda name: f"/usr/bin/{name}",
                ),
                mock.patch(
                    "scripts.verify_release_outputs._run_tool",
                    side_effect=[verify_result, cert_result],
                ),
            ):
                self.assertEqual([TEST_DIGEST], verify_aab_signature(aab, TEST_DIGEST))

    def test_aab_signature_rejects_unexpected_certificate(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            aab = Path(temp_dir) / "app-release.aab"
            aab.write_bytes(b"signed-artifact-placeholder")
            verify_result = subprocess.CompletedProcess(
                args=["jarsigner"],
                returncode=0,
                stdout="jar verified.\n",
                stderr="",
            )
            cert_result = subprocess.CompletedProcess(
                args=["keytool"],
                returncode=0,
                stdout=f"Certificate fingerprints:\n\t SHA256: {COLON_DIGEST}\n",
                stderr="",
            )
            with (
                mock.patch(
                    "scripts.verify_release_outputs.shutil.which",
                    side_effect=lambda name: f"/usr/bin/{name}",
                ),
                mock.patch(
                    "scripts.verify_release_outputs._run_tool",
                    side_effect=[verify_result, cert_result],
                ),
            ):
                with self.assertRaisesRegex(RuntimeError, "AAB signer certificate SHA-256 mismatch"):
                    verify_aab_signature(aab, OTHER_DIGEST)

    def test_signature_evidence_is_normalized_and_stable(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            output = Path(temp_dir) / "signatures.txt"
            write_signature_evidence(output, [TEST_DIGEST], [OTHER_DIGEST])
            self.assertEqual(
                "# SudokuNova release signer certificate SHA-256 evidence\n"
                f"apk_signer_sha256={TEST_DIGEST}\n"
                f"aab_signer_sha256={OTHER_DIGEST}\n",
                output.read_text(encoding="utf-8"),
            )


if __name__ == "__main__":
    unittest.main()
