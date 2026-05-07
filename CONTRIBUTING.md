# Contributing to Budget Manager App

Thank you for your interest in contributing! This document explains how to set up the project locally, submit changes, and follow the project's coding conventions.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Development Setup](#development-setup)
3. [Project Structure](#project-structure)
4. [Coding Conventions](#coding-conventions)
5. [Running Tests](#running-tests)
6. [Submitting Changes](#submitting-changes)
7. [Reporting Bugs](#reporting-bugs)
8. [Feature Requests](#feature-requests)

---

## Getting Started

### Prerequisites

| Tool | Minimum Version | Notes |
|------|----------------|-------|
| Java JDK | 11 | OpenJDK or Oracle JDK both fine |
| Git | 2.x | Any recent version |
| JUnit | 4.13 | Only needed for running tests |

### Fork and Clone

```bash
# Fork the repo on GitHub, then:
git clone https://github.com/YOUR_USERNAME/Budget-manager-App.git
cd Budget-manager-App
git remote add upstream https://github.com/Harsh7115/Budget-manager-App.git
```

---

## Development Setup

The project uses no build system (Maven/Gradle) — just plain `javac`.

### Compile

```bash
# From the repo root, compile all Java files:
javac -cp . *.java
```

### Run the App

```bash
java -cp . Main
```

### Run Tests

Download JUnit 4 if you don't have it:

```bash
# Download JUnit 4 and Hamcrest to a local lib/ directory
mkdir -p lib
curl -L https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar -o lib/junit-4.13.2.jar
curl -L https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -o lib/hamcrest-core-1.3.jar

# Compile everything including tests
javac -cp .:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar *.java

# Run all test classes
java -cp .:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar \
  org.junit.runner.JUnitCore \
  UserTest ExpenseTest BudgetManagerTest ReportManagerTest DataPersistenceTest
```

Expected output: all tests passing with no failures.

---

## Project Structure

```
Budget-manager-App/
├── Main.java                    # Application entry point
├── FinanceApp.java              # Root JFrame; builds tabbed layout
│
├── # --- Models / Business Logic ---
├── User.java                    # User entity + SHA-256 auth
├── ExpenseManager.java          # CRUD for transactions + filtering
├── BudgetManager.java           # Monthly budget allocation + alerts
├── ReportManager.java           # Aggregate queries + chart data
├── DataPersistenceManager.java  # Flat-file read/write
├── TransactionFileHandler.java  # CSV import/export
│
├── # --- UI Panels (Swing) ---
├── DashboardPanel.java          # Summary + recent transactions
├── ExpenseTrackerPanel.java     # Add/edit/delete expenses
├── BudgetManagerPanel.java      # Progress bars + budget config
├── ReportManagerPanel.java      # Pie charts + date-range reports
├── SettingsPanel.java           # Password change, account mgmt
│
└── # --- Tests ---
    ├── UserTest.java
    ├── ExpenseTest.java
    ├── BudgetManagerTest.java
    ├── ReportManagerTest.java
    └── DataPersistenceTest.java
```

---

## Coding Conventions

### General

- **Java style**: Follow standard Java naming conventions.
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **Indentation**: 4 spaces (no tabs).
- **Line length**: Keep lines under 120 characters.
- **Braces**: K&R style — opening brace on the same line.

```java
// GOOD
public void addExpense(Expense e) {
    if (e == null) {
        throw new IllegalArgumentException("Expense cannot be null");
    }
    expenses.add(e);
}

// BAD
public void addExpense(Expense e)
{
    if(e==null)
    throw new IllegalArgumentException("Expense cannot be null");
    expenses.add(e);
}
```

### Swing UI

- All Swing updates must happen on the **Event Dispatch Thread** (EDT):
  ```java
  SwingUtilities.invokeLater(() -> panel.refresh());
  ```
- Avoid blocking the EDT — move file I/O or heavy computation to a `SwingWorker`.
- Use layout managers (no absolute positioning).

### Business Logic

- Keep UI panels thin — delegate logic to the corresponding `*Manager` class.
- `DataPersistenceManager` is the single point of contact for disk I/O.

### Tests

- Every new public method in a `*Manager` class should have at least one unit test.
- Test class naming: `<ClassName>Test.java`
- Use descriptive test method names: `testAddExpenseIncreasesCount()`, not `test1()`.

---

## Running Tests

```bash
# Quick check — compile + run all tests
javac -cp .:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar *.java && \
java -cp .:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar \
  org.junit.runner.JUnitCore \
  UserTest ExpenseTest BudgetManagerTest ReportManagerTest DataPersistenceTest
```

All tests must pass before you open a pull request.

---

## Submitting Changes

1. **Create a branch** from `main`:
   ```bash
   git checkout -b feat/my-feature
   ```

2. **Make your changes** following the conventions above.

3. **Write or update tests** for any changed behavior.

4. **Commit** with a conventional message:
   ```
   feat: add recurring expense support
   fix: correct negative balance display in dashboard
   refactor: extract CategoryFilter into its own class
   docs: update CONTRIBUTING with test instructions
   ```

5. **Push** your branch and open a pull request against `main`.

6. **Describe your PR**: what changed, why, and how to test it.

---

## Reporting Bugs

Open a GitHub Issue and include:

- **Steps to reproduce** — what did you do?
- **Expected behavior** — what should happen?
- **Actual behavior** — what happened instead?
- **Java version** (`java -version`)
- **OS** (Windows / macOS / Linux)
- A screenshot if the bug is visual.

---

## Feature Requests

Open a GitHub Issue with the `enhancement` label. Include:

- What problem does this solve?
- What would the user experience look like?
- Any alternatives you considered.

---

## Code of Conduct

Be respectful and constructive. Contributions of all experience levels are welcome.
