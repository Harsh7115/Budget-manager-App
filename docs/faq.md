# Frequently Asked Questions

This document answers the questions that have come up most often while
using Budget Manager. If your question is not covered here, please open an
issue — the FAQ grows as real questions arrive.

## General

### Does Budget Manager need an internet connection?

No. Everything runs locally. The app reads and writes plain text files in
the directory you choose at first launch (default: `~/.budget-manager`).
There is no cloud component and no telemetry.

### Where is my data stored?

By default the data directory is `~/.budget-manager`. Inside you will
find:

- `users.txt` — user accounts (username + salted password hash)
- `transactions.txt` — one transaction per line (CSV-like format)
- `budgets.txt` — per-category budgets
- `settings.properties` — UI preferences

All files are human-readable. You can back them up with any copy tool
(Time Machine, rsync, a ZIP, anything).

### Can I change the data directory?

Yes. Pass `--data-dir /path/to/dir` on the command line, or edit the
"Data directory" field under Settings. The app will move the existing
files to the new location on the next save.

## Accounts and security

### Where are passwords stored?

Passwords are never stored. Budget Manager stores a PBKDF2-HMAC-SHA256
hash of the password with a per-user salt (see `User.hashPassword` in
`User.java`). The default iteration count is 120,000. Even with physical
access to `users.txt`, recovering the original password is expensive.

### I forgot my password — can I reset it?

Because we do not store the password, there is no "send me a reset link"
path. You have two options:

1. Create a new account and re-import your transactions from a CSV
   export, if you made one.
2. Manually delete the offending line in `users.txt`. You will lose
   only that user's account, not their transactions (those are keyed by
   user ID, not password).

### Can I share an account between two computers?

Yes — copy the data directory between machines. The file format is
versioned (`SCHEMA_VERSION` at the top of every file), so mismatched
versions will refuse to load rather than silently corrupt data.

## Budgets and categories

### How are budgets enforced?

They are soft limits. When you add an expense that would push a category
over its budget, the app flags the entry in red and shows a warning
banner — it does not block the entry. The intent is to help you see
reality, not to fight with you over it.

### Can I have nested categories (e.g. Food > Groceries, Food > Dining)?

Not yet. The data model currently uses a flat category list. Issue #27
tracks the proposal to add subcategories; design help welcome.

### How do I delete a category I created by mistake?

Go to Settings → Categories → Right-click → Delete. Any existing
transactions tagged with that category will be moved to `Uncategorized`
rather than dropped.

## Reports

### Why is last month's report empty?

Reports are generated from transactions whose date falls within the
requested range. If you just imported data but the dates on the
imported lines are outside your selected month, the report will be
empty. Check the "Date" column in Expense Tracker.

### Can I export a report?

Yes — File → Export Report → CSV or PDF. The CSV exporter matches the
column order of the in-app table so it round-trips cleanly.

## Development

### How do I run the tests?

```
mvn test
```

See `docs/testing.md` for the full JUnit setup, including how to run
a single test class or method.

### Which Java version do I need?

Java 17 or newer. The CI workflow (`.github/workflows/ci.yml`) tests
against Temurin 17 and 21.

### Can I contribute?

Please do. Small PRs (docs, tests, UI polish) are especially welcome.
If you want to tackle something bigger, open an issue first so we can
sketch the approach before you invest real time.
