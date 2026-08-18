import json
from pathlib import Path
import tempfile
import unittest
import zipfile

from scripts.verify_release_outputs import (
    AAB_REQUIRED_ENTRIES,
    APK_REQUIRED_ENTRIES,
    load_apk_metadata,
    validate_zip,
    write_manifest,
)


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


if __name__ == "__main__":
    unittest.main()
