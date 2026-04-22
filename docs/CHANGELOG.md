# Changelog

All notable changes to **Budget Manager** are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned
- CSV/Excel export for expense and budget reports
- Dark mode support via system look-and-feel detection
- Multi-currency support with live exchange-rate polling
- Cloud sync via optional SQLite to remote REST API bridge

---

## [1.3.0] - 2026-04-20

### Added
- **CONTRIBUTING guide** (`docs/contributing.md`) with full PR workflow,
  coding style (Google Java Style Guide), and issue templates.
- **FAQ document** (`docs/faq.md`) covering storage format, password reset,
  budget roll-over behaviour, and report generation questions.

### Changed
- CI matrix now includes Java 21 alongside Java 17; the artifact upload step
  uploads the built JAR on every successful main-branch build.

### Fixed
- `ReportManager` no longer throws `NullPointerException` when the expense
  list is empty at the time a monthly report is generated (fixes #42).

---

## [1.2.0] - 2026-04-12

### Added
- **Comprehensive architecture doc** (`docs/ARCHITECTURE.md`) covering the
  MVC layer diagram, Swing EDT rules, and the persistence contract between
  `DataPersistenceManager` and the file system.
- `SettingsPanel` — new UI panel for user preferences (currency symbol,
  date format, default budget period).

### Changed
- `DashboardPanel` now refreshes automatically when an expense is added or
  deleted via an observer pattern rather than requiring a manual tab switch.
- `TransactionFileHandler` writes atomically by staging to a `.tmp` file
  and renaming on success, preventing data loss on crash.

### Deprecated
- Direct access to `BudgetManager.getExpenseList()` — callers should use
  `ExpenseManager.getAll()` instead. The old method will be removed in v2.0.

---

## [1.1.0] - 2026-04-06

### Added
- **UI components reference** (`docs/ui-components.md`) — full table of all
  Swing panels, dialogs, and custom widgets with constructor signatures.
- Observability hooks — `DataPersistenceManager` now logs read/write
  durations at `FINE` level for diagnostic purposes.
- JUnit 4 test suites for `ReportManager`, `ExpenseManager`, and `User`.

### Changed
- Budget period selector changed from a `JTextField` to a `JSpinner`
  (month/year) to prevent invalid date entry.

### Fixed
- `ExpenseTrackerPanel` table no longer resets scroll to top after every
  row insertion (#31).
- Date sorting now correctly handles the December to January boundary (#33).

---

## [1.0.1] - 2026-03-30

### Fixed
- `DataPersistenceManager.load()` silently skipped malformed lines; it now
  logs a warning with the line number and continues (#27).
- Amount fields no longer accept non-numeric input on paste (#28).

---

## [1.0.0] - 2026-03-22

### Added
- Initial public release.
- **Core features**: expense tracking, budget management, monthly reports,
  user authentication (local password hash), dashboard overview.
- **Persistence**: JSON flat-file storage via `DataPersistenceManager`.
- **UI**: Swing tabbed layout with Dashboard, ExpenseTracker, BudgetManager,
  ReportManager, and Settings panels.
- **Testing**: JUnit 4 suite for `Expense`, `User`, `ExpenseManager`.
- **CI**: GitHub Actions workflow building with Maven on Java 17.
- **Documentation**: setup guide, data model reference, architecture overview.

---

## Legend

| Symbol | Meaning |
|--------|---------|
| Added | New feature or file |
| Changed | Change in existing functionality |
| Deprecated | Soon-to-be-removed feature |
| Removed | Removed feature |
| Fixed | Bug fix |
| Security | Vulnerability fix |

[Unreleased]: https://github.com/Harsh7115/Budget-manager-App/compare/v1.3.0...HEAD
[1.3.0]: https://github.com/Harsh7115/Budget-manager-App/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/Harsh7115/Budget-manager-App/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/Harsh7115/Budget-manager-App/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/Harsh7115/Budget-manager-App/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Harsh7115/Budget-manager-App/releases/tag/v1.0.0
