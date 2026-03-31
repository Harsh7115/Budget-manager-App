# Data Model

This document describes every persistent entity in Budget Manager App, the
flat-file format used to store them, and the in-memory object model that
mirrors that storage.

---

## Overview

Budget Manager App uses **file-based persistence** — no embedded database.
All data lives in plain text files managed by `DataPersistenceManager.java`.
On startup the app reads these files into memory; on every mutating operation
it writes them back atomically (write-to-temp then rename).

```
data/
├── users.dat          # one User record per line
├── expenses.dat       # one Expense record per line (all users)
├── budgets.dat        # one Budget record per line (all users)
└── settings.dat       # one key=value pair per line
```

> The `data/` directory is created automatically on first run next to the
> compiled `.class` files (or the JAR).

---

## Entities

### User

Represents a registered account. Passwords are stored as **SHA-256 hex
digests** — never in plaintext.

| Field | Java type | Description |
|---|---|---|
| `username` | `String` | Primary key — unique, case-sensitive |
| `passwordHash` | `String` | SHA-256 hex of the user's password |
| `displayName` | `String` | Friendly name shown in the UI |
| `createdAt` | `long` | Unix epoch ms of account creation |

**File format** (`users.dat`):

```
<username>|<passwordHash>|<displayName>|<createdAt>
```

Example:
```
alice|2bd806c9...f3a8|Alice Smith|1707480000000
bob|0a041b9...c2f5|Bob Jones|1708000000000
```

---

### Expense

A single financial transaction entered by a user.

| Field | Java type | Description |
|---|---|---|
| `id` | `String` | UUID v4 — globally unique |
| `username` | `String` | FK → User.username |
| `amount` | `double` | Positive value in USD |
| `category` | `String` | Free-form label (e.g. "Food", "Rent") |
| `description` | `String` | User-entered note (may be empty) |
| `date` | `long` | Unix epoch ms of the transaction |

**File format** (`expenses.dat`):

```
<id>|<username>|<amount>|<category>|<description>|<date>
```

Example:
```
a1b2c3d4-...|alice|42.50|Food|Grocery run|1743292800000
e5f6g7h8-...|alice|1200.00|Rent|March rent|1743379200000
```

**Notes**
- `description` may contain spaces but **must not** contain the pipe character `|`.
- `amount` is always serialised with full double precision; the UI rounds to
  two decimal places for display.
- Entries are appended in insertion order; `ExpenseManager` sorts them by
  `date` descending before presenting to the UI.

---

### Budget

A monthly spending limit for one category, scoped to a single user.

| Field | Java type | Description |
|---|---|---|
| `username` | `String` | FK → User.username |
| `category` | `String` | Must match an Expense category |
| `limit` | `double` | Maximum spend allowed for the month |
| `month` | `int` | 1-based month number (1 = January) |
| `year` | `int` | Four-digit calendar year |

**File format** (`budgets.dat`):

```
<username>|<category>|<limit>|<month>|<year>
```

Example:
```
alice|Food|600.00|3|2026
alice|Rent|1500.00|3|2026
bob|Entertainment|200.00|3|2026
```

**Notes**
- The composite key `(username, category, month, year)` is unique.
  `BudgetManager` enforces this by overwriting the old record on upsert.
- `BudgetManagerPanel` computes the **spent** amount by summing matching
  `Expense` records in real time — it is not stored.

---

### Settings

Application-level preferences, stored as simple key-value pairs.

| Key | Type | Default | Description |
|---|---|---|---|
| `theme` | String | `light` | UI theme (`light` or `dark`) |
| `currency` | String | `USD` | ISO 4217 currency code for display |
| `dateFormat` | String | `MM/dd/yyyy` | Java `SimpleDateFormat` pattern |
| `defaultCategory` | String | `Uncategorized` | Pre-filled category in the expense form |

**File format** (`settings.dat`):

```
<key>=<value>
```

Example:
```
theme=dark
currency=USD
dateFormat=MM/dd/yyyy
defaultCategory=Food
```

---

## In-memory object model

```
DataPersistenceManager
 ├── List<User>      users       (loaded once at startup)
 ├── List<Expense>   expenses    (loaded once at startup)
 ├── List<Budget>    budgets     (loaded once at startup)
 └── Map<String,String> settings
```

All four collections live in `DataPersistenceManager` as instance fields.
The panel classes (e.g. `ExpenseTrackerPanel`) call the manager's read/write
methods — they never touch the files directly.

Write operations follow this pattern to avoid corruption on crash:

```java
// Pseudo-code for atomic write
File tmp = new File(path + ".tmp");
writeAllLines(tmp, records);
tmp.renameTo(new File(path));   // atomic on POSIX; best-effort on Windows
```

---

## CSV import / export

`TransactionFileHandler` supports moving expense data in and out via CSV.

**Import format** (first row is the header):

```csv
date,amount,category,description
2026-03-01,42.50,Food,Grocery run
2026-03-05,1200.00,Rent,March rent
```

- `date` must be `yyyy-MM-dd`.
- Rows with parse errors are skipped and logged to `System.err`.
- Imported rows are assigned fresh UUIDs and stamped with the current user.

**Export format** mirrors import, using the same column order.
