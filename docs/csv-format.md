# CSV Import / Export Format

Budget Manager can move transaction data in and out of the app via CSV files.
This document specifies the exact format `TransactionFileHandler` reads and writes,
so that bank exports or third-party tools can be reshaped to match.

## File-level rules

- The file must be UTF-8 encoded. A UTF-8 BOM at the start of the file is tolerated and stripped.
- Line endings may be LF or CRLF; the importer normalizes them.
- The first non-empty line must be the header row described below.
- Empty lines and lines beginning with `#` are skipped.
- The delimiter is a comma. Quoting follows RFC 4180: a field containing a comma,
  a double-quote, or a newline must be wrapped in double quotes, with embedded
  double-quotes escaped by doubling them.

## Header row

The header is fixed and case-sensitive:

```
date,category,amount,description,account
```

Extra columns are preserved verbatim on a round-trip but are not surfaced in the UI.
Missing required columns abort the import with a row-level error message in the
import dialog.

## Column reference

| Column        | Type    | Required | Notes                                                                 |
|---------------|---------|----------|-----------------------------------------------------------------------|
| `date`        | string  | yes      | ISO-8601 calendar date, `YYYY-MM-DD`. No time component.              |
| `category`    | string  | yes      | Free-form. Matched case-insensitively against the user's category set.|
| `amount`      | decimal | yes      | Two decimal places. Negative values are treated as refunds / income.  |
| `description` | string  | no       | Up to 256 characters. Empty if absent.                                |
| `account`     | string  | no       | Defaults to `default` if absent.                                      |

## Example

```
date,category,amount,description,account
2026-04-01,Groceries,52.18,"Trader Joe's",checking
2026-04-02,Rent,1450.00,April rent,checking
2026-04-03,Salary,-3200.00,Paycheck,checking
2026-04-04,Coffee,4.75,"Latte, oat milk",credit
```

## Import behavior

1. The file is parsed in a single pass.
2. Each row is validated against the schema above. Bad rows are collected and reported
   together at the end; partial imports are not committed.
3. If every row is valid, transactions are appended to the active user's expense list
   and the budget thresholds for the affected categories are recomputed.
4. The persistence layer is then asked to flush, so an import survives a crash before
   the next manual save.

## Export behavior

Exports always write the columns above in the same order, regardless of how the
transaction was originally captured. This guarantees that an exported file can be
imported back into Budget Manager without loss.

Exports do **not** include user authentication data, budget definitions, or
category color settings; those live in the user profile and are out of scope for
the transaction CSV.

## Common pitfalls

- Spreadsheets often coerce dates into the local locale's format. Re-format the
  `date` column as `YYYY-MM-DD` before importing.
- Bank exports sometimes use a positive amount for spending and a separate
  `Credit/Debit` column. Convert these to a signed amount in the `amount` column.
- Excel may strip leading zeros from category codes; quote them or rename the
  category to a non-numeric label before import.
