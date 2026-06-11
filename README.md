# Budget Manager App

[![Java](https://img.shields.io/badge/Java-11-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=flat)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![JUnit](https://img.shields.io/badge/Tests-JUnit%204-green?style=flat)](https://junit.org/junit4/)
[![License: MIT](https://img.shields.io/badge/License-MIT-lightgrey?style=flat)](LICENSE)

Java desktop personal finance app — user auth, expense tracking, monthly budgets, charts, and CSV import/export. Built with Swing; full JUnit test suite.

## Features

- **Auth** — account creation and login with SHA-256 password hashing
- **Expense Tracking** — add, edit, delete, and filter transactions by category or date range
- **Budget Management** — set monthly limits per category with visual progress bars
- **Financial Reports** — pie charts and trend analysis across time periods
- **CSV Import / Export** — move transaction data in and out
- **Local Persistence** — all data stored to flat files; no external DB

## Getting Started

**Requirements:** Java 11+

```bash
git clone https://github.com/Harsh7115/Budget-manager-App
cd Budget-manager-App

# Compile
javac *.java

# Run
java Main
```

**First run:**
1. Click **Create Account** and register
2. Log in with your credentials
3. Add expenses under the **Expenses** tab
4. Set monthly limits under **Budget**
5. View spending breakdowns under **Reports**

### Run Tests

```bash
javac -cp .:junit-4.13.jar:hamcrest-core-1.3.jar *.java
java  -cp .:junit-4.13.jar:hamcrest-core-1.3.jar \
  org.junit.runner.JUnitCore UserTest ExpenseTest BudgetManagerTest ReportManagerTest
```

## Architecture

```
Main
 └── FinanceApp (JFrame)
       ├── DashboardPanel        recent transactions + spending summary
       ├── ExpenseTrackerPanel   add / edit / delete / filter expenses
       ├── BudgetManagerPanel    per-category limits + live progress bars
       ├── ReportManagerPanel    pie charts + time-series analysis
       └── SettingsPanel         account management

Core managers (no UI dependency):
  User                  model + SHA-256 auth
  ExpenseManager        CRUD + filtering logic
  BudgetManager         allocation + threshold alerts
  ReportManager         aggregation + chart data preparation
  DataPersistenceManager  read/write flat files
  TransactionFileHandler  CSV import/export
```

## Source Files

| File | Role |
|---|---|
| `Main.java` | Entry point |
| `FinanceApp.java` | Main tabbed window |
| `User.java` | User model, password hashing (SHA-256) |
| `ExpenseManager.java` | Expense CRUD and filter logic |
| `BudgetManager.java` | Budget allocation and overspend detection |
| `ReportManager.java` | Aggregation and chart data |
| `DataPersistenceManager.java` | Flat-file read/write for all entities |
| `TransactionFileHandler.java` | CSV import and export |
| `DashboardPanel.java` | Overview UI |
| `ExpenseTrackerPanel.java` | Expense management UI |
| `BudgetManagerPanel.java` | Budget progress UI |
| `ReportManagerPanel.java` | Charts and analytics UI |
| `SettingsPanel.java` | Account settings UI |
| `*Test.java` | JUnit 4 test classes |

## Data Model

```
User        { username, passwordHash }
Expense     { id, userId, amount, category, description, date }
Budget      { userId, category, monthlyLimit, month }
```

All entities serialized to line-delimited flat files under a local `data/` directory. `DataPersistenceManager` handles load-on-start and save-on-change.

## Tech Stack

Java 11 · Java Swing · JUnit 4 · SHA-256 (via `java.security.MessageDigest`)
