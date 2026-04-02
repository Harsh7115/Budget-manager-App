# Testing Guide

This document explains how to run, extend, and interpret the JUnit 4 test
suite for Budget Manager App. It covers the project test layout, how to
compile and run tests from the command line (or Maven), how to write new
tests, and guidelines for keeping the suite fast and reliable.

---

## Table of Contents

1. [Test Layout](#test-layout)
2. [Running Tests — Maven (recommended)](#running-tests--maven-recommended)
3. [Running Tests — Command Line (no Maven)](#running-tests--command-line-no-maven)
4. [Test Class Reference](#test-class-reference)
5. [Writing New Tests](#writing-new-tests)
6. [Test Utilities and Helpers](#test-utilities-and-helpers)
7. [Coverage](#coverage)
8. [CI Integration](#ci-integration)

---

## Test Layout

All test files live in the project root alongside their production counterparts
(the project does not yet use a `src/test/` Maven layout — sources are being
migrated).

| Test class              | Tests                                              |
|-------------------------|----------------------------------------------------|
| `UserTest.java`         | Account creation, login, password hashing, duplicate detection |
| `ExpenseTest.java`      | Expense model validation — amounts, categories, dates |
| `ExpenseManagerTest.java` | CRUD operations, filtering by date/category, totals |
| `BudgetManagerTest.java`  | Budget allocation, over-budget detection, resets  |
| `ReportManagerTest.java`  | Spending summaries, category breakdowns, period filtering |
| `DataPersistenceTest.java`| Save/load round-trips, corrupt-file handling       |

---

## Running Tests — Maven (recommended)

The project ships a `pom.xml` so Maven is the simplest way to compile and run
all tests in one command.

```bash
# Run the full test suite
mvn test

# Run a specific test class
mvn test -Dtest=UserTest

# Run a single test method
mvn test -Dtest=BudgetManagerTest#testOverBudgetAlert

# Skip tests (compile only)
mvn package -DskipTests
```

Test results are written to `target/surefire-reports/`. Open any `.txt` file
there for a human-readable summary, or the `.xml` files for machine-readable
output (useful in CI).

---

## Running Tests — Command Line (no Maven)

If you prefer to compile and run manually, download the JUnit 4 and Hamcrest
jars first:

```bash
# Download JUnit 4 + Hamcrest (one-time)
mkdir -p lib
curl -sSL -o lib/junit-4.13.2.jar \
  https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
curl -sSL -o lib/hamcrest-core-1.3.jar \
  https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

# Compile production sources
mkdir -p out
javac -d out *.java

# Compile test sources
javac -cp out:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar \
      -d out \
      *Test.java

# Run all tests
java -cp out:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar \
     org.junit.runner.JUnitCore \
     UserTest ExpenseTest ExpenseManagerTest BudgetManagerTest \
     ReportManagerTest DataPersistenceTest
```

On Windows replace `:` with `;` in classpath separators.

---

## Test Class Reference

### UserTest

Verifies user account management:

- `testCreateAccount` — happy-path account creation returns non-null user.
- `testDuplicateUsername` — creating two accounts with the same username throws
  `IllegalArgumentException`.
- `testLoginSuccess` — correct credentials return the user object.
- `testLoginWrongPassword` — wrong password returns `null` (not an exception).
- `testPasswordHashing` — stored password hash is not the plain-text value.

### ExpenseManagerTest

Covers CRUD and filtering on the expense list:

- `testAddExpense` — added expense appears in `getAllExpenses()`.
- `testDeleteExpense` — removed expense no longer appears.
- `testFilterByCategory` — only expenses with the given category are returned.
- `testFilterByDateRange` — expenses outside the range are excluded.
- `testTotalCalculation` — sum of all expense amounts is computed correctly.

### BudgetManagerTest

Tests budget allocation and threshold logic:

- `testSetBudget` — setting a budget for a category persists correctly.
- `testOverBudgetAlert` — spending more than the budget triggers an over-budget
  flag.
- `testMonthlyReset` — calling `resetMonthly()` zeroes all spent amounts.
- `testUnknownCategory` — accessing a category with no budget returns 0.

### ReportManagerTest

- `testSpendingByCategory` — map of category → total matches expected values.
- `testMonthlyTrend` — list of monthly totals has the expected length and sums.
- `testEmptyExpenseList` — report on zero expenses returns empty/zero data
  without throwing.

---

## Writing New Tests

### Skeleton

```java
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyFeatureTest {

    private MyFeature feature;

    @Before
    public void setUp() {
        // Runs before each @Test method.
        feature = new MyFeature();
    }

    @Test
    public void testHappyPath() {
        String result = feature.process("valid input");
        assertEquals("expected output", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInput() {
        feature.process(null);  // should throw
    }
}
```

### Conventions

- Test method names follow the pattern `test<WhatIsBeingTested>` in camelCase.
- Each test has a single logical assertion; split multi-concern tests into
  separate methods.
- Use `@Before` for shared set-up and `@After` for teardown (e.g., deleting
  temporary data files written by persistence tests).
- Avoid hard-coded file paths — use `File.createTempFile()` for any I/O tests.
- Do not depend on test execution order; every test must be self-contained.

---

## Test Utilities and Helpers

The test suite includes a small set of factory helpers to reduce boilerplate:

```java
// Create a User with deterministic credentials for testing
User u = TestFactory.user("alice", "s3cr3t");

// Create a pre-populated ExpenseManager with 5 sample expenses
ExpenseManager em = TestFactory.expenseManagerWithSamples();

// Write a temporary data file and return its path
String path = TestFactory.tempDataFile(expenseList);
```

These are defined in `TestFactory.java` (to be added — contributions welcome).

---

## Coverage

To generate an HTML coverage report with JaCoCo via Maven:

```bash
mvn test jacoco:report
open target/site/jacoco/index.html
```

The `pom.xml` includes the JaCoCo plugin configuration. Current coverage
targets (enforced in CI):

| Package           | Line coverage target |
|-------------------|----------------------|
| Model classes     | ≥ 90 %               |
| Manager classes   | ≥ 80 %               |
| UI panel classes  | ≥ 40 % (Swing is hard to unit-test) |

---

## CI Integration

Every push and pull request to `main` triggers the GitHub Actions workflow
defined in `.github/workflows/ci.yml`. The `test` job:

1. Checks out the code.
2. Sets up JDK 17 and 21 (matrix build).
3. Runs `mvn test`.
4. Uploads Surefire XML reports as a workflow artifact.

If a test fails in CI but passes locally, common causes are:

- **File path assumptions** — the CI runner checks out to a fresh directory.
  Use relative paths or `System.getProperty("java.io.tmpdir")`.
- **Locale/timezone differences** — CI runs in UTC; avoid `new Date()` in
  assertions. Use fixed timestamps instead.
- **Swing initialisation** — Swing tests that call `new JFrame()` may fail in
  headless mode. Guard with `GraphicsEnvironment.isHeadless()` or move UI
  construction to a separate EDT-safe method.

*Last updated: April 2026*
