"""Regression tests for stable-tag, installer and release-asset publishing gates."""

from __future__ import annotations

from pathlib import Path
import tempfile
import unittest
from zipfile import ZipFile

from scripts.release_artifacts import (
    check_tag,
    create_checksum_manifest,
    stage_asset,
    validate_binary,
)


class ReleaseAssetTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        (self.root / "app").mkdir()
        (self.root / "sharedUI").mkdir()
        (self.root / "app/build.gradle.kts").write_text(
            'applicationId = "in.sanskar.sudokunova"\n'
            'minSdk = 26\ntargetSdk = 37\nversionCode = 2020\n'
            'versionName = "0.2.0"\n', encoding="utf-8"
        )
        (self.root / "sharedUI/build.gradle.kts").write_text(
            'packageVersion = "0.2.0"\n', encoding="utf-8"
        )

    def _archive(self, path: Path, name: str = "index.html") -> None:
        path.parent.mkdir(parents=True, exist_ok=True)
        with ZipFile(path, "w") as z:
            z.writestr(name, "SudokuNova test asset")

    def _populate_assets(self) -> Path:
        directory = self.root / "release-assets"
        directory.mkdir()
        prefix = "SudokuNova-v0.2.0-"
        for suffix in ("android-DEBUG.apk", "web.zip", "ios-simulator-framework.zip"):
            self._archive(directory / (prefix + suffix))
        (directory / (prefix + "linux.deb")).write_bytes(b"!<arch>\ncontent")
        (directory / (prefix + "windows.msi")).write_bytes(
            bytes.fromhex("d0cf11e0a1b11ae1") + b"payload"
        )
        (directory / (prefix + "macos.dmg")).write_bytes(b"dmg-data")
        return directory

    def test_source_and_desktop_versions_must_match_tag(self) -> None:
        self.assertEqual("0.2.0", check_tag("v0.2.0", self.root))
        for tag in ("v0.2.1", "v0.2.0;rm", "v0.02.0", "../v0.2.0", "v0.2.0-rc1"):
            with self.subTest(tag=tag), self.assertRaises(ValueError):
                check_tag(tag, self.root)

    def test_web_archive_preserves_root_index(self) -> None:
        source = self.root / "sharedUI/build/dist/wasmJs/productionExecutable"
        source.mkdir(parents=True)
        (source / "index.html").write_text("<title>SudokuNova</title>", encoding="utf-8")
        (source / "app.js").write_text("void 0;", encoding="utf-8")
        artifact = stage_asset("web", "v0.2.0", self.root, self.root / "out")
        with ZipFile(artifact) as z:
            self.assertEqual(["app.js", "index.html"], sorted(z.namelist()))
        self.assertTrue(artifact.name.endswith("-web.zip"))

    def test_web_without_entry_point_fails_closed(self) -> None:
        source = self.root / "sharedUI/build/dist/wasmJs/productionExecutable"
        source.mkdir(parents=True)
        (source / "app.js").write_text("void 0;", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "index.html"):
            stage_asset("web", "v0.2.0", self.root, self.root / "out")

    def test_unsigned_android_release_is_not_distributed(self) -> None:
        source = self.root / "app/build/outputs/apk/release"
        source.mkdir(parents=True)
        (source / "app-release-unsigned.apk").write_bytes(b"unsigned")
        with self.assertRaisesRegex(ValueError, "exactly one"):
            stage_asset("android-signed", "v0.2.0", self.root, self.root / "out")

    def test_duplicate_installer_outputs_are_rejected(self) -> None:
        source = self.root / "sharedUI/build/compose/binaries/main/dmg"
        source.mkdir(parents=True)
        (source / "a.dmg").write_bytes(b"a")
        (source / "b.dmg").write_bytes(b"b")
        with self.assertRaisesRegex(ValueError, "exactly one"):
            stage_asset("macos", "v0.2.0", self.root, self.root / "out")

    def test_ios_framework_requires_real_framework_directory(self) -> None:
        source = self.root / "sharedUI/build/bin/iosSimulatorArm64/debugFramework"
        source.mkdir(parents=True)
        (source / "readme.txt").write_text("not a framework", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "framework is missing"):
            stage_asset("ios-simulator", "v0.2.0", self.root, self.root / "out")

    def test_manifest_checks_required_platforms_and_hashes(self) -> None:
        directory = self._populate_assets()
        manifest = create_checksum_manifest("v0.2.0", directory)
        text = manifest.read_text(encoding="utf-8")
        self.assertEqual(6, len([line for line in text.splitlines() if "  " in line]))
        self.assertIn("SudokuNova-v0.2.0-windows.msi", text)

    def test_partial_signed_bundle_rejected(self) -> None:
        directory = self._populate_assets()
        self._archive(directory / "SudokuNova-v0.2.0-android-signed.apk")
        with self.assertRaisesRegex(ValueError, "published together"):
            create_checksum_manifest("v0.2.0", directory)

    def test_unexpected_asset_rejected(self) -> None:
        directory = self._populate_assets()
        (directory / "secret.txt").write_text("do not publish", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "unknown assets"):
            create_checksum_manifest("v0.2.0", directory)

    def test_corrupted_zip_rejected(self) -> None:
        path = self.root / "invalid.zip"
        path.write_bytes(b"not a zip")
        with self.assertRaisesRegex(ValueError, "Invalid ZIP-based asset"):
            validate_binary(path)


if __name__ == "__main__":
    unittest.main()
