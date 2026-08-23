import tempfile
import unittest
from pathlib import Path

from scripts.verify_translations import (
    collect,
    collect_placeholders,
    parity_errors,
    placeholder_parity_errors,
)


class TranslationVerifierTest(unittest.TestCase):
    def test_collects_all_shared_strings_without_prefix_filter(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            (directory / "strings.xml").write_text(
                """<?xml version=\"1.0\" encoding=\"utf-8\"?>
<resources>
    <string name=\"app_name\">SudokuNova</string>
    <string name=\"status_ready\">Ready</string>
</resources>
""",
                encoding="utf-8",
            )

            strings = collect(directory)

            self.assertEqual({"app_name", "status_ready"}, set(strings))

    def test_android_prefix_filter_remains_scoped(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            (directory / "strings.xml").write_text(
                """<resources>
    <string name=\"difficulty_easy\">Easy</string>
    <string name=\"unrelated\">Other</string>
</resources>
""",
                encoding="utf-8",
            )

            strings = collect(directory, ("difficulty_",))

            self.assertEqual({"difficulty_easy"}, set(strings))

    def test_reports_missing_and_hindi_only_keys(self) -> None:
        english_file = Path("values/strings.xml")
        hindi_file = Path("values-hi/strings.xml")
        errors = parity_errors(
            {"shared": english_file, "missing": english_file},
            {"shared": hindi_file, "extra": hindi_file},
            label="shared",
        )

        rendered = "\n".join(errors)
        self.assertIn("Missing Hindi resources in shared", rendered)
        self.assertIn("missing", rendered)
        self.assertIn("Hindi-only resources without English base in shared", rendered)
        self.assertIn("extra", rendered)

    def test_rejects_empty_english_catalog(self) -> None:
        errors = parity_errors({}, {}, label="shared")

        self.assertEqual(["No localization keys were found in shared."], errors)

    def test_collects_normalized_placeholder_signatures(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            directory = Path(temp_dir)
            (directory / "strings.xml").write_text(
                """<resources>
    <string name=\"cell\">Row %1$d, column %2$d, %3$s</string>
    <string name=\"plain\">Ready</string>
</resources>
""",
                encoding="utf-8",
            )

            signatures = collect_placeholders(directory)

            self.assertEqual(("1:d", "2:d", "3:s"), signatures["cell"])
            self.assertEqual((), signatures["plain"])

    def test_placeholder_order_may_change_when_positions_match(self) -> None:
        errors = placeholder_parity_errors(
            {"cell": ("1:d", "2:d")},
            {"cell": ("1:d", "2:d")},
            label="shared",
        )

        self.assertEqual([], errors)

    def test_reports_placeholder_mismatch(self) -> None:
        errors = placeholder_parity_errors(
            {"cell": ("1:d", "2:d")},
            {"cell": ("1:d", "2:s")},
            label="shared",
        )

        self.assertEqual(1, len(errors))
        self.assertIn("Placeholder mismatch in shared for cell", errors[0])


if __name__ == "__main__":
    unittest.main()
