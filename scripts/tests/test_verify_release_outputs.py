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
    load_apk_metadata,
    normalize_cert_sha256,
    parse_apksigner_cert_sha256,
    parse_keytool_cert_sha256,
    validate_zip,
    verify_aab_signature,
    verify_apk_signature,
    write_manifest,
    write_signature_evidence,
)

TEST_DIGEST = "11" * 32
OTHER_DIGEST = "22" * 32
COLON_DIGEST = ":".join(TEST_DIGEST[i : i + 2] for i in range(0, len(TEST_DIGEST), 2)).upper()


class ReleaseOutputVerifierTest(unittest.TestCase):
    def make_zip(self, path: Path, entries: set[str]) -> None:
        with zipfile.ZipFile(path, "w") as archive:
            for entry in sorted(entries):
                archive.writestr(entry, b"test")

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
            metadata.write_text(
                json.dumps(
                    {
                        "elements": [
                            {
                                "versionCode": 1000,
                                "versionName": "1.0.0-rc.1",
                            }
                        ]
                    }
                ),
                encoding="utf-8",
            )

            self.assertEqual((1000, "1.0.0-rc.1"), load_apk_metadata(metadata))

    def test_rejects_multiple_apk_metadata_elements(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            metadata = Path(temp_dir) / "output-metadata.json"
            metadata.write_text(
                json.dumps(
                    {
                        "elements": [
                            {"versionCode": 1000, "versionName": "1.0.0-rc.1"},
                            {"versionCode": 1001, "versionName": "1.0.0"},
                        ]
                    }
                ),
                encoding="utf-8",
            )

            with self.assertRaisesRegex(ValueError, "exactly one release element"):
                load_apk_metadata(metadata)

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

    def test_normalizes_colon_separated_certificate_fingerprint(self) -> None:
        self.assertEqual(TEST_DIGEST, normalize_cert_sha256(COLON_DIGEST))

    def test_rejects_invalid_certificate_fingerprint(self) -> None:
        with self.assertRaisesRegex(ValueError, "64 hexadecimal"):
            normalize_cert_sha256("not-a-fingerprint")

    def test_parses_apksigner_certificate_digest(self) -> None:
        output = f"Signer #1 certificate SHA-256 digest: {COLON_DIGEST}\n"
        self.assertEqual([TEST_DIGEST], parse_apksigner_cert_sha256(output))

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
                stdout=f"Signer #1 certificate SHA-256 digest: {COLON_DIGEST}\n",
                stderr="",
            )
            with (
                mock.patch("scripts.verify_release_outputs.shutil.which", return_value="/sdk/apksigner"),
                mock.patch("scripts.verify_release_outputs._run_tool", return_value=completed),
            ):
                self.assertEqual([TEST_DIGEST], verify_apk_signature(apk, TEST_DIGEST))

    def test_apk_signature_rejects_unexpected_certificate(self) -> None:
        completed = subprocess.CompletedProcess(
            args=["apksigner"],
            returncode=0,
            stdout=f"Signer #1 certificate SHA-256 digest: {COLON_DIGEST}\n",
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
