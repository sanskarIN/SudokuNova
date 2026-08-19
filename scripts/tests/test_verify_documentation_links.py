from pathlib import Path
import tempfile
import unittest

from scripts.verify_documentation_links import (
    extract_markdown_targets,
    find_broken_links,
    resolve_local_target,
)


class DocumentationLinkVerifierTest(unittest.TestCase):
    def test_accepts_existing_relative_and_root_relative_files(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            docs = root / "docs"
            docs.mkdir()
            (root / "README.md").write_text("# Root\n", encoding="utf-8")
            (docs / "guide.md").write_text(
                "[Root](../README.md)\n[Root absolute](/README.md#top)\n",
                encoding="utf-8",
            )

            self.assertEqual([], find_broken_links(root))

    def test_rejects_missing_local_file(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            source = root / "README.md"
            source.write_text("[Missing](docs/missing.md)\n", encoding="utf-8")

            broken = find_broken_links(root)

            self.assertEqual(1, len(broken))
            self.assertEqual("docs/missing.md", broken[0].target)

    def test_rejects_link_that_escapes_repository_root(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir) / "repo"
            root.mkdir()
            outside = root.parent / "outside.md"
            outside.write_text("outside", encoding="utf-8")
            source = root / "README.md"
            source.write_text("[Outside](../outside.md)\n", encoding="utf-8")

            broken = find_broken_links(root)

            self.assertEqual(1, len(broken))
            self.assertEqual("../outside.md", broken[0].target)

    def test_ignores_external_mail_and_anchor_links(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            (root / "README.md").write_text(
                "\n".join(
                    [
                        "[Web](https://example.com/path)",
                        "[Mail](mailto:test@example.com)",
                        "[Phone](tel:+10000000000)",
                        "[Anchor](#section)",
                        "[Protocol relative](//example.com/file)",
                    ]
                ),
                encoding="utf-8",
            )

            self.assertEqual([], find_broken_links(root))

    def test_supports_angle_bracket_destination_and_optional_title(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            (root / "target.md").write_text("target", encoding="utf-8")
            (root / "README.md").write_text(
                '[Angle](<target.md> "Target")\n[Title](target.md "Target")\n',
                encoding="utf-8",
            )

            self.assertEqual([], find_broken_links(root))

    def test_supports_reference_definition_targets(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            (root / "target.md").write_text("target", encoding="utf-8")
            (root / "README.md").write_text(
                "[Guide][guide]\n\n[guide]: target.md\n",
                encoding="utf-8",
            )

            self.assertEqual([], find_broken_links(root))

    def test_extracts_regular_and_image_targets(self) -> None:
        targets = list(
            extract_markdown_targets(
                "[Doc](docs/a.md) ![Image](assets/icon.png)\n[ref]: docs/b.md\n"
            )
        )
        self.assertIn("docs/a.md", targets)
        self.assertIn("assets/icon.png", targets)
        self.assertIn("docs/b.md", targets)

    def test_resolves_percent_encoded_local_path(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            source = root / "README.md"
            target = root / "file name.md"
            target.write_text("target", encoding="utf-8")

            resolved = resolve_local_target(root, source, "file%20name.md#section")

            self.assertEqual(target.resolve(), resolved)

    def test_ignores_build_and_dot_git_markdown(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            (root / "README.md").write_text("# Root\n", encoding="utf-8")
            build = root / "build"
            build.mkdir()
            (build / "generated.md").write_text("[Missing](nope.md)", encoding="utf-8")
            git = root / ".git"
            git.mkdir()
            (git / "internal.md").write_text("[Missing](nope.md)", encoding="utf-8")

            self.assertEqual([], find_broken_links(root))


if __name__ == "__main__":
    unittest.main()
