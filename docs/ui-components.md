# UI Components Reference

This document catalogues every Swing panel and dialog in Budget Manager App, describing their layout, key widgets, and the controller events they fire.

---

## AppFrame

`AppFrame` is the top-level `JFrame`. It owns a `CardLayout` panel that swaps child panels in place, a `JMenuBar` for navigation, and the shared status bar at the bottom.

```
AppFrame (JFrame)
  +-- JMenuBar
  |     +-- File  (New, Save, Exit)
  |     +-- View  (Dashboard, Transactions, Budgets, Reports)
  |     +-- Help  (About)
  +-- cardPanel (CardLayout)
  |     +-- LoginPanel
  |     +-- DashboardPanel
  |     +-- TransactionPanel
  |     +-- BudgetPanel
  |     +-- ReportPanel
  +-- statusBar (JLabel)
```

Navigation is triggered by calling `AppFrame.showCard(name)`, which delegates to `CardLayout.show()`.

---

## LoginPanel

Handles user authentication before the main UI is accessible.

| Widget | Type | Purpose |
|---|---|---|
| `usernameField` | `JTextField` | Username input |
| `passwordField` | `JPasswordField` | Password input (masked) |
| `loginButton` | `JButton` | Calls `AuthService.login()` |
| `errorLabel` | `JLabel` | Displays invalid-credential messages |

**Events fired:** `LoginEvent` on success → `AppFrame.showCard("dashboard")`.

---

## DashboardPanel

The landing screen after login. Shows a monthly spending summary and a bar chart.

| Widget | Type | Purpose |
|---|---|---|
| `monthPicker` | `JComboBox<YearMonth>` | Selects the active month |
| `totalIncomeLabel` | `JLabel` | Sum of income transactions |
| `totalExpenseLabel` | `JLabel` | Sum of expense transactions |
| `netLabel` | `JLabel` | Income minus expenses |
| `categoryChart` | `JFreeChart` panel | Bar chart: spending per category |
| `recentTable` | `JTable` | Last 10 transactions |

Refreshed whenever `TransactionService` or `BudgetService` data changes via a `PropertyChangeListener`.

---

## TransactionPanel

Full CRUD interface for income and expense entries.

### Toolbar

| Button | Action |
|---|---|
| Add | Opens `TransactionDialog` in CREATE mode |
| Edit | Opens `TransactionDialog` in EDIT mode for selected row |
| Delete | Prompts confirmation, then calls `TransactionService.delete()` |
| Filter | Expands the filter bar |

### Filter Bar

| Widget | Type | Filters by |
|---|---|---|
| `dateFrom` / `dateTo` | `JDateChooser` | Date range |
| `categoryFilter` | `JComboBox` | Category |
| `typeFilter` | `JComboBox` | INCOME / EXPENSE |
| `searchField` | `JTextField` | Notes full-text |

### Transaction Table

A `JTable` backed by `TransactionTableModel extends AbstractTableModel`. Columns: Date, Type, Category, Amount, Notes. Rows are sortable via `TableRowSorter`.

---

## TransactionDialog

Modal `JDialog` for creating or editing a single transaction.

| Widget | Type | Validation |
|---|---|---|
| `dateField` | `JDateChooser` | Must not be null |
| `typeCombo` | `JComboBox` | INCOME or EXPENSE |
| `categoryCombo` | `JComboBox` | Non-empty selection |
| `amountField` | `JFormattedTextField` | Positive decimal |
| `notesArea` | `JTextArea` | Optional, max 200 chars |
| `saveButton` | `JButton` | Commits if validation passes |

---

## BudgetPanel

Displays per-category budget limits alongside actual spending and a progress bar.

| Widget | Type | Purpose |
|---|---|---|
| `monthPicker` | `JComboBox<YearMonth>` | Month selector |
| `budgetTable` | `JTable` | Category, Limit, Spent, Remaining, % Used |
| `addBudgetButton` | `JButton` | Opens `BudgetDialog` |
| `editBudgetButton` | `JButton` | Opens `BudgetDialog` for selected row |

Rows where spending exceeds the limit are highlighted in red via a custom `DefaultTableCellRenderer`.

---

## ReportPanel

Generates and displays period summaries; supports CSV export.

| Widget | Type | Purpose |
|---|---|---|
| `fromDate` / `toDate` | `JDateChooser` | Report date range |
| `groupByCombo` | `JComboBox` | Group by: Month / Category / Type |
| `generateButton` | `JButton` | Calls `ReportService.generate()` |
| `reportTable` | `JTable` | Aggregated results |
| `exportButton` | `JButton` | Writes results to CSV via `JFileChooser` |

---

## Look and Feel

The application uses the system look-and-feel (`UIManager.getSystemLookAndFeelClassName()`) so it renders natively on Windows, macOS, and Linux. Font size is scaled to 13 px across all panels for consistency.

---

## Adding a New Panel

1. Create `MyPanel extends JPanel` in `com.budgetmanager.view`.
2. Register it in `AppFrame`: `cardPanel.add(new MyPanel(...), "myPanel")`.
3. Add a menu item or button that calls `AppFrame.showCard("myPanel")`.
4. Wire controller events to the appropriate service methods.
