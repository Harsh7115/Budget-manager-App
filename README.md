# Budget Manager App

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=flat)
![JUnit](https://img.shields.io/badge/Tested%20with-JUnit-green?style=flat)
![License](https://img.shields.io/badge/license-MIT-lightgrey?style=flat)

A Java desktop personal finance application with user authentication, expense tracking, budget management, and financial report generation. Built with Swing and follows object-oriented design principles throughout.

## Screenshots

| Dashboard | Expense Tracker | Reports |
|-----------|----------------|---------|
| ![dashboard](dashboard.png) | ![expense](expense.png) | ![report](report.png) |

## Features

- **User Authentication** — secure login with hashed passwords and account management
- **Expense Tracking** — add, edit, filter, and categorize transactions
- **Budget Management** — set monthly budgets per category with visual progress indicators
- **Financial Reports** — pie charts and spending analysis across time periods
- **CSV Import/Export** — move transaction data in and out of the app
- **Data Persistence** — all data saved locally via file-based storage
- **JUnit Test Suite** — tests covering auth, expenses, budgets, and persistence

## Project Structure

```
Budget-manager-App/
├── Main.java                    # Entry point
├── FinanceApp.java              # Main tabbed application window
├── User.java                    # User model + auth (password hashing)
├── ExpenseManager.java          # Expense CRUD + filtering logic
├── BudgetManager.java           # Budget allocation + threshold tracking
├── ReportManager.java           # Report generation + chart data
├── DataPersistenceManager.java  # File-based storage (read/write)
├── TransactionFileHandler.java  # CSV import/export
├── DashboardPanel.java          # Overview UI — recent transactions + summary
├── ExpenseTrackerPanel.java     # Expense management UI
├── BudgetManagerPanel.java      # Budget progress UI
├── ReportManagerPanel.java      # Charts + analytics UI
├── SettingsPanel.java           # User account settings
└── *Test.java                   # JUnit tests
```

## Getting Started

**Requirements:** Java 11+, JUnit 4 (tests only)

```bash
# Compile all sources
javac *.java

# Run the app
java Main

# Run tests
java -cp .:junit-4.13.jar org.junit.runner.JUnitCore UserTest ExpenseTest BudgetManagerTest ReportManagerTest
```

**First run:**
1. Click "Create Account" and register
2. Log in with your credentials
3. Start adding expenses under the **Expense** tab
4. Set monthly budgets under **Budget**
5. View spending breakdowns under **Reports**

## Tech Stack

| Area | Technology |
|------|-----------|
| Language | Java 11 |
| GUI | Java Swing |
| Charts | Custom Swing rendering |
| Storage | File-based (flat file) |
| Auth | SHA-256 password hashing |
| Testing | JUnit 4 |

<!-- Last reviewed: March 2026 -->
