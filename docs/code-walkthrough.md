# Code Walkthrough

A guided tour of the Budget-manager-App source tree, intended for new contributors who want to know where to add their first feature.

## Source Tree

```
src/
  main/java/com/budget/
    model/        — domain entities (User, Transaction, Budget, Category)
    view/         — Swing JFrame and JPanel components
    controller/   — wires user actions to model updates
    persistence/  — file-backed storage (CSV + serialized state)
    util/         — cross-cutting helpers (date math, formatters)
  test/java/com/budget/
    … mirrored test packages
```

## Reading Order

When reading the codebase for the first time, follow this sequence:

1. Start in `model/Transaction.java` — the smallest entity. Note the value-type pattern (no setters, `equals`/`hashCode` on all fields).
2. Move to `model/Budget.java` to see how transactions are aggregated per category per month.
3. Read `controller/TransactionController.java` to see how a UI event flows into the model.
4. Open `view/DashboardFrame.java` to see how the panels are wired and where the controller is invoked.
5. Skim `persistence/CsvRepository.java` for the file format used to persist user data.

## Where to Add Things

### A new field on Transaction

Update `Transaction` (model + tests), then `CsvRepository` to read/write the new column, then `TransactionDialog` to expose it in the form, then `ReportFrame` if it should appear in summaries.

### A new screen

Create a `JFrame` under `view/` and a matching controller under `controller/`. Wire it from `DashboardFrame` or `MainMenu`. Reuse `util/Formatters` for currency/date display.

### A new validation rule

Add the rule to the relevant `Validator` under `controller/validation/` and a JUnit test that covers both the valid and invalid case. Validators are intentionally pure functions — no IO, no Swing.

## Conventions

- `final` fields and constructor injection over setters.
- All money values use `BigDecimal` — never `double`.
- Dates use `java.time.LocalDate`. Avoid `java.util.Date`.
- Logging via `java.util.logging` (no third-party logger pulled in for the desktop build).
- Tests live next to the production package (mirrored layout). Class name pattern: `TransactionTest`.

## Common Pitfalls

- Forgetting to update `CsvRepository` when adding a model field — the test `CsvRepositoryRoundTripTest` will catch this.
- Using `JOptionPane` from the controller — keep UI confined to `view/`. Controllers raise events that the view turns into dialogs.
- Shipping a budget with a negative cap — `BudgetValidator` rejects this; the dialog should display the error.
