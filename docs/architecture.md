# Budget Manager App — Architecture

## Overview

Budget Manager is a Java desktop application built with **Swing** and structured around the
**Model-View-Controller (MVC)** pattern. All persistence is file-based (JSON via a custom
`TransactionFileHandler`), and the JUnit 5 test suite covers every model and manager class.

---

## Package Structure

```
BudgetManagerApp/
├── Main.java                   # Entry point — bootstraps the Swing event-dispatch thread
├── FinanceApp.java             # Top-level JFrame; owns the tab layout and navigation
│
├── model/
│   ├── Expense.java            # Immutable value object: amount, category, date, note
│   ├── User.java               # Authenticated user entity (name, hashed password)
│   └── BudgetManager.java      # Domain aggregate: budget limits keyed by category
│
├── manager/
│   ├── ExpenseManager.java     # CRUD + filtering for Expense records
│   ├── ReportManager.java      # Aggregation & summary statistics over expenses
│   └── DataPersistenceManager.java  # Serialise/deserialise the full app state to disk
│
├── ui/
│   ├── DashboardPanel.java     # Home screen: balance summary + recent transactions
│   ├── ExpenseTrackerPanel.java# Add/edit/delete expenses; table view with sorting
│   ├── BudgetManagerPanel.java # Set per-category budget limits; progress bars
│   ├── ReportManagerPanel.java # Charts and tabular breakdowns (monthly / category)
│   └── SettingsPanel.java      # User preferences and account management
│
├── io/
│   └── TransactionFileHandler.java  # Reads and writes JSON state files
│
└── test/
    ├── BudgetManagerTest.java
    ├── ExpenseManagerTest.java
    ├── ExpenseTest.java
    ├── ReportManagerTest.java
    └── UserTest.java
```

---

## Layered Architecture

```
┌─────────────────────────────────────────┐
│              UI Layer (Swing)            │
│  DashboardPanel  ExpenseTrackerPanel     │
│  BudgetManagerPanel  ReportManagerPanel  │
└────────────────┬────────────────────────┘
                 │ calls
┌────────────────▼────────────────────────┐
│            Manager Layer                 │
│  ExpenseManager   ReportManager          │
│  BudgetManager    DataPersistenceManager │
└────────────────┬────────────────────────┘
                 │ owns / transforms
┌────────────────▼────────────────────────┐
│            Model Layer                   │
│  Expense   User   (plain Java objects)   │
└────────────────┬────────────────────────┘
                 │ serialised by
┌────────────────▼────────────────────────┐
│            I/O Layer                     │
│  TransactionFileHandler (JSON on disk)   │
└─────────────────────────────────────────┘
```

The UI panels **never** access the model directly; they always go through a manager.
This makes the managers fully unit-testable without spinning up any Swing components.

---

## Key Design Decisions

### 1. File-based persistence
State is persisted as a single JSON file managed by `DataPersistenceManager`.
On startup the file is loaded once; on any mutation the manager flushes the updated
state back to disk. This keeps the I/O surface small and avoids an embedded database
dependency.

### 2. Immutable `Expense` value objects
`Expense` instances are immutable after construction. Edits are handled by deleting the
old record and inserting a new one through `ExpenseManager`. This makes equality
checks trivial and prevents accidental mutation through UI references.

### 3. Observer-free, pull-based UI refresh
Panels call `repopulate()` on themselves after every write operation rather than
subscribing to a change event bus. This is intentionally simple for a desktop app of
this scope; a future refactor could introduce a `PropertyChangeListener` or reactive
model if the panel count grows.

### 4. Authentication via `User`
`User` holds a BCrypt-style hashed password. `SettingsPanel` validates credentials
before allowing any destructive action (clear all data, change password). The
`UserTest` suite verifies hash round-trips and equality semantics.

---

## Data Flow — Adding an Expense

```
User fills form in ExpenseTrackerPanel
        │
        ▼
ExpenseTrackerPanel.onSubmit()
        │  creates Expense value object
        ▼
ExpenseManager.addExpense(expense)
        │  appends to in-memory list
        ▼
DataPersistenceManager.save()
        │  serialises full state → JSON file
        ▼
ExpenseTrackerPanel.repopulate()
        │  re-reads from ExpenseManager
        ▼
Table view refreshed
```

---

## Testing Strategy

All tests live alongside the source and are run via Maven (`mvn test`).
CI (GitHub Actions) runs the full suite on every push to `main`.

| Test class              | What it covers                                      |
|-------------------------|-----------------------------------------------------|
| `ExpenseTest`            | Construction, equality, immutability                |
| `ExpenseManagerTest`     | CRUD operations, category filtering, date range     |
| `BudgetManagerTest`      | Setting limits, over-budget detection               |
| `ReportManagerTest`      | Monthly totals, category breakdown aggregations     |
| `UserTest`               | Password hashing, credential validation             |

---

## Build & Run

```bash
# Compile and run all tests
mvn test

# Package a runnable JAR
mvn package

# Launch the app
java -jar target/BudgetManagerApp-*.jar
```

See [docs/setup.md](setup.md) for environment prerequisites and IDE configuration.
