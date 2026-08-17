# Documentation Roadmap

The repository-level [`ROADMAP.md`](../ROADMAP.md) is the source of truth for product/version milestones. This file tracks documentation maturity.

## v0.1 Documentation

- [x] Documentation index
- [x] Getting started
- [x] Installation
- [x] Development setup
- [x] Architecture
- [x] Sudoku engine
- [x] Puzzle generation
- [x] Difficulty system
- [x] Game rules
- [x] UI / UX
- [x] Design system
- [x] Accessibility
- [x] Localization
- [x] Testing
- [x] Security design
- [x] Privacy policy
- [x] Data storage
- [x] Backup/restore status
- [x] Building
- [x] Releasing
- [x] Contributor extension guide
- [x] Troubleshooting
- [x] FAQ
- [x] Release checklist
- [x] QA matrix
- [x] Changelog guide

## Before v1.0

Documentation must expand when implementation expands:

- Full game-history schema/query documentation
- Room database migration guide once Room is introduced
- Daily Challenge archive behavior
- Saved/favorite puzzle model
- Import/export file format and threat model
- Shareable puzzle-code specification
- Complete supported hint-technique reference
- Hindi localization contributor notes
- Instrumentation/UI-test guide with actual suite inventory
- Performance benchmark methodology
- Release signing CI configuration once safely implemented
- Store asset preparation using actual screenshots

## Accuracy Rule

Documentation must never claim a planned feature is implemented. When source behavior changes, update the relevant doc in the same pull request whenever practical.

For ongoing implementation detail, use `../what_changed.md`. For user-visible release history, use `../CHANGELOG.md`.
