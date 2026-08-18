from __future__ import annotations

import importlib.util
import sys
import tempfile
import unittest
import zipfile
from pathlib import Path
from unittest import mock

SCRIPT_PATH = Path(__file__).resolve().parents[1] / "verify_release_artifacts.py"
SPEC = importlib.util.spec_from_file_location("verify_release_artifacts", SCRIPT_PATH)
assert SPEC is not None and SPEC.loader is not None
MODULE = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = MODULE
SPEC.loader.exec_module(MODULE)


class VerifyReleaseArtifactsTest(unittest.TestCase):
    def make_zip(self, directory: Path, name: str) -> Path:
        path = directory / name
        with zipfile.ZipFile(path, "w") as archive:
            archive.writestr("payload.txt", "SudokuNova")
        return path

    def test_valid_apk_gets_checksum_without_signature_requirement(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            artifact = self.make_zip(Path(temp), "app-release.apk")
            with mock.patch.object(MODULE.shutil, "which", return_value=None):
                result = MODULE.verify_artifact(artifact)
            self.assertEqual(result.path, artifact.resolve())
            self.assertEqual(len(result.sha256), 64)
            self.assertIn("not checked", result.signature_status)

    def test_valid_aab_can_write_deterministic_checksum_file(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            artifact = self.make_zip(root, "app-release.aab")
            with mock.patch.object(MODULE.shutil, "which", return_value=None):
                result = MODULE.verify_artifact(artifact)
            output = root / "SHA256SUMS"
            MODULE.write_checksums([result], output)
            self.assertEqual(output.read_text(encoding="utf-8"), f"{result.sha256}  app-release.aab\n")

    def test_missing_artifact_is_rejected(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            with self.assertRaises(ValueError):
                MODULE.verify_artifact(Path(temp) / "missing.apk")

    def test_unsupported_extension_is_rejected(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "artifact.zip"
            path.write_bytes(b"not relevant")
            with self.assertRaises(ValueError):
                MODULE.verify_artifact(path)

    def test_corrupt_apk_is_rejected(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "broken.apk"
            path.write_bytes(b"not a zip")
            with self.assertRaises(ValueError):
                MODULE.verify_artifact(path)

    def test_signature_requirement_fails_when_tool_is_missing(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            artifact = self.make_zip(Path(temp), "app-release.apk")
            with mock.patch.object(MODULE.shutil, "which", return_value=None):
                with self.assertRaises(RuntimeError):
                    MODULE.verify_artifact(artifact, require_signature=True)


if __name__ == "__main__":
    unittest.main()
