# Contributing to Budget Manager App

Thank you for your interest in contributing! This document covers everything you need to get set up, understand the codebase conventions, and submit quality pull requests.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Project Structure](#project-structure)
3. [Code Style](#code-style)
4. [Running Tests](#running-tests)
5. [Submitting a Pull Request](#submitting-a-pull-request)
6. [Reporting Bugs](#reporting-bugs)
7. [Requesting Features](#requesting-features)

---

## Getting Started

### Prerequisites

| Tool | Minimum Version |
|------|----------------|
| JDK  | 17             |
| Maven | 3.8+          |
| Git  | 2.x            |

### Local Setup

```bash
# 1. Fork and clone
git clone https://github.com/<your-username>/Budget-manager-App.git
cd Budget-manager-App

# 2. Build and run all tests
mvn clean verify

# 3. Run the application
mvn exec:java -Dexec.mainClass="Main"
```

> **Headless environments:** The Swing UI requires a display. In CI or WSL, set
> `export DISPLAY=:0` or use Xvfb before running the app. Tests mock the UI
> layer and run headlessly without any extra setup.

---

## Project Structure

```
Budget-manager-App/
├── src/                         # (future) Maven standard layout
├── *.java                       # Application source files (flat layout for now)
├── *Test.java                   # JUnit 4 test classes
├── pom.xml                      # Maven build descriptor
├── docs/                        # Developer documentation
│   ├── architecture.md
│   ├── data-model.md
│   ├── testing.md
│   ├── ui-components.md
│   └── contributing.md          # ← you are here
└── .github/workflows/ci.yml     # GitHub Actions CI
```

Key classes and their responsibilities:

| Class | Responsibility |
|-------|---------------|
| `Main.java` | Entry point — creates `FinanceApp` |
| `FinanceApp.java` | Main `JFrame` with tab navigation |
| `ExpenseManager.java` | Expense CRUD and filtering logic |
| `BudgetManager.java` | Budget allocation and threshold checks |
| `ReportManager.java` | Aggregation and chart data generation |
| `DataPersistenceManager.java` | File-based read/write for all entities |
| `User.java` | User model + SHA-256 password hashing |

---

## Code Style

We follow standard Java conventions with a few project-specific rules:

### Naming

- **Classes** — `UpperCamelCase` (e.g., `ExpenseManager`)
- **Methods and variables** — `lowerCamelCase` (e.g., `addExpense`)
- **Constants** — `UPPER_SNAKE_CASE` (e.g., `MAX_BUDGET_CATEGORIES`)
- **Test methods** — `test<MethodUnderTest>_<scenario>` (e.g., `testAddExpense_duplicateEntry`)

### General Rules

- Keep methods under 40 lines. Extract helper methods freely.
- Every public method must have a Javadoc comment with at least a one-line summary.
- Avoid magic numbers — use named constants or enum values.
- Prefer `final` on local variables and parameters where practical.
- No raw types (use generics — `List<Expense>`, not just `List`).

### Swing / UI Code

- All UI mutations must happen on the **Event Dispatch Thread** (EDT). Use
  `SwingUtilities.invokeLater()` when updating components from non-EDT threads.
- Keep business logic out of panel classes — panels call manager methods,
  they do not contain business rules themselves.

---

## Running Tests

```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ExpenseManagerTest

# Run with verbose output
mvn test -Dsurefire.useFile=false
```

Tests are located alongside source files (e.g., `ExpenseManagerTest.java`
tests `ExpenseManager.java`). See [testing.md](testing.md) for a full guide,
including coverage expectations and how to write new test cases.

**CI** runs the full test suite on Java 17 and 21 on every push and PR.
A failing CI build will block merging.

---

## Submitting a Pull Request

1. **Branch naming** — use a descriptive prefix:
   - `feat/<short-description>` for new features
   - `fix/<short-description>` for bug fixes
   - `docs/<short-description>` for documentation
   - `refactor/<short-description>` for refactoring
   - `test/<short-description>` for test additions

2. **Commit messages** — follow [Conventional Commits](https://www.conventionalcommits.org/):
   ```
   feat: add CSV export for filtered expense views
   fix: prevent negative budget threshold values
   docs: expand setup guide with Maven troubleshooting
   ```

3. **Before opening the PR**, make sure:
   - [ ] `mvn clean verify` passes locally
   - [ ] New code has corresponding JUnit tests
   - [ ] Public methods have Javadoc
   - [ ] No debug/print statements left in

4. **PR description** — fill in the template:
   - What does this change do?
   - Why is it needed?
   - How was it tested?
   - Screenshots for UI changes

---

## Reporting Bugs

Open a GitHub Issue with the **Bug** label and include:

- Java version (`java -version`)
- Operating system
- Steps to reproduce
- Expected vs. actual behavior
- Stack trace (if any)

---

## Requesting Features

Open a GitHub Issue with the **Enhancement** label. Describe:

- The use case / problem it solves
- Proposed behavior
- Any alternatives considered

For larger features, consider opening a discussion first so we can align on
design before implementation begins.

---

*Happy coding!*
