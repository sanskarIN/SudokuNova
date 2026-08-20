import unittest

from scripts.verify_documentation_coverage import (
    coverage_for_path,
    render_markdown,
    validate_coverage,
    validate_documentation_index,
)


class DocumentationCoverageVerifierTest(unittest.TestCase):
    def test_resolves_specific_repository_areas(self) -> None:
        cases = {
            "app/src/androidTest/java/example/Test.kt": "Android connected and instrumentation tests",
            "app/src/test/java/example/Test.kt": "Android JVM unit tests",
            "app/schemas/in.sanskar.sudokunova.data.SudokuDatabase/2.json": "Room schema history",
            "app/src/main/res/values/strings.xml": "Android localized, visual, and XML resources",
            "app/src/main/java/com/sanskar/sudokunova/MainActivity.kt": "Android application source and manifest",
            "sharedUI/src/commonTest/kotlin/example/SharedStateTest.kt": "Shared multiplatform UI tests",
            "sharedUI/src/commonMain/kotlin/example/SharedApp.kt": "Shared multiplatform UI implementation and resources",
            "sharedUI/src/wasmJsMain/resources/index.html": "Shared multiplatform UI implementation and resources",
            "sharedUI/build.gradle.kts": "Shared multiplatform UI module configuration",
            "iosApp/ContentView.swift": "Apple SwiftUI host sources",
            "sudoku-engine/src/test/kotlin/example/SolverTest.kt": "Sudoku engine tests",
            "sudoku-engine/src/main/kotlin/example/Solver.kt": "Sudoku engine implementation",
            "macrobenchmark/src/main/java/example/StartupBenchmark.kt": "Macrobenchmark harness",
            "scripts/tests/test_example.py": "Repository guard regression tests",
            "scripts/verify_example.py": "Repository verification and maintenance scripts",
            ".github/workflows/ci.yml": "GitHub Actions workflows",
            ".github/ISSUE_TEMPLATE/bug_report.yml": "GitHub collaboration and repository metadata",
            "gradle/libs.versions.toml": "Gradle wrapper and version catalog",
            "build.gradle.kts": "Root build configuration",
            ".editorconfig": "Repository editor and ignore configuration",
            "README.md": "Root project, policy, and community documents",
            "docs/README.md": "Detailed documentation library",
        }

        for path, expected_name in cases.items():
            with self.subTest(path=path):
                rule = coverage_for_path(path)
                self.assertIsNotNone(rule)
                self.assertEqual(expected_name, rule.name)

    def test_rejects_unknown_top_level_path(self) -> None:
        self.assertIsNone(coverage_for_path("unexpected/new-area/file.txt"))

    def test_accepts_covered_files_when_canonical_documents_are_tracked(self) -> None:
        paths = ["README.md", "docs/README.md"]
        tracked = {
            *paths,
            "docs/DOCUMENTATION_STANDARDS.md",
        }

        results, errors = validate_coverage(paths, tracked)

        self.assertEqual([], errors)
        self.assertEqual(paths, [result.path for result in results])

    def test_accepts_multiplatform_files_when_cross_platform_docs_are_tracked(self) -> None:
        paths = [
            "sharedUI/src/commonMain/kotlin/example/SharedApp.kt",
            "iosApp/SudokuNovaApp.swift",
        ]
        tracked = {
            *paths,
            "docs/CROSS_PLATFORM.md",
            "docs/ARCHITECTURE.md",
            "docs/PROJECT_STRUCTURE.md",
            "docs/BUILDING.md",
        }

        results, errors = validate_coverage(paths, tracked)

        self.assertEqual([], errors)
        self.assertEqual(paths, [result.path for result in results])

    def test_rejects_rule_whose_canonical_document_is_not_tracked(self) -> None:
        paths = ["README.md"]
        tracked = {"README.md", "docs/README.md"}

        results, errors = validate_coverage(paths, tracked)

        self.assertEqual([], results)
        self.assertEqual(1, len(errors))
        self.assertIn("docs/DOCUMENTATION_STANDARDS.md", errors[0])

    def test_rejects_uncovered_tracked_file(self) -> None:
        paths = ["new-module/src/main/example.kt"]

        results, errors = validate_coverage(paths, paths)

        self.assertEqual([], results)
        self.assertEqual(
            ["Tracked file has no documentation coverage rule: new-module/src/main/example.kt"],
            errors,
        )

    def test_documentation_index_accepts_every_linked_detailed_guide(self) -> None:
        tracked = {
            "docs/README.md",
            "docs/BUILDING.md",
            "docs/REPOSITORY_GUARDS.md",
        }
        index_text = """
# Docs

- [Building](BUILDING.md)
- [Repository guards](./REPOSITORY_GUARDS.md#coverage)
"""

        errors = validate_documentation_index(tracked, index_text)

        self.assertEqual([], errors)

    def test_documentation_index_rejects_hidden_detailed_guide(self) -> None:
        tracked = {
            "docs/README.md",
            "docs/BUILDING.md",
            "docs/UNINDEXED_GUIDE.md",
        }
        index_text = "[Building](BUILDING.md)"

        errors = validate_documentation_index(tracked, index_text)

        self.assertEqual(
            ["Detailed documentation file is not linked from docs/README.md: docs/UNINDEXED_GUIDE.md"],
            errors,
        )

    def test_documentation_index_ignores_root_markdown_and_docs_index_itself(self) -> None:
        tracked = {
            "README.md",
            "what_changed.md",
            "docs/README.md",
        }

        errors = validate_documentation_index(tracked, "# Documentation")

        self.assertEqual([], errors)

    def test_markdown_report_lists_each_resolved_file_and_documents(self) -> None:
        tracked = {
            "docs/README.md",
            "docs/DOCUMENTATION_STANDARDS.md",
            "README.md",
        }
        results, errors = validate_coverage(["README.md"], tracked)

        self.assertEqual([], errors)
        report = render_markdown(results)
        self.assertIn("`README.md`", report)
        self.assertIn("Root project, policy, and community documents", report)
        self.assertIn("`docs/README.md`", report)

    def test_windows_separator_is_normalized_without_dropping_dot_prefix(self) -> None:
        rule = coverage_for_path(r".github\workflows\ci.yml")

        self.assertIsNotNone(rule)
        self.assertEqual("GitHub Actions workflows", rule.name)


if __name__ == "__main__":
    unittest.main()
