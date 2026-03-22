# Budget Manager App — Setup & Developer Guide

This guide covers everything needed to build, run, and contribute to **Budget Manager App**: a Java desktop personal-finance application built with Swing and JUnit.

---

## Prerequisites

| Tool        | Minimum version | Notes                                      |
|-------------|-----------------|---------------------------------------------|
| JDK         | 17              | Any distribution (Temurin, Zulu, Oracle)   |
| Maven       | 3.8             | Or use the included `mvnw` wrapper          |
| Git         | 2.x             | For cloning and branch management          |
| (optional) IntelliJ IDEA | 2023+ | Recommended IDE; Eclipse also works   |

---

## Quick start

```bash
# 1. Clone
git clone https://github.com/Harsh7115/Budget-manager-App.git
cd Budget-manager-App

# 2. Build and run all tests
mvn clean verify

# 3. Launch the application
mvn exec:java -Dexec.mainClass="com.budgetmanager.Main"
```

On first launch the app creates a local SQLite database file (`budget.db`) in the working directory. No external database setup is required.

---

## Project structure

```
Budget-manager-App/
├── src/
│   ├── main/
│   │   └── java/com/budgetmanager/
│   │       ├── Main.java               # entry point, creates MainFrame
│   │       ├── model/                  # domain objects (Transaction, Budget, Category)
│   │       ├── dao/                    # data-access layer (JDBC + SQLite)
│   │       ├── service/                # business logic (BudgetService, ReportService)
│   │       └── ui/                     # Swing panels and frames
│   │           ├── MainFrame.java
│   │           ├── DashboardPanel.java
│   │           ├── TransactionPanel.java
│   │           ├── BudgetPanel.java
│   │           └── ReportPanel.java
│   └── test/
│       └── java/com/budgetmanager/
│           ├── service/                # JUnit 5 unit tests for services
│           └── dao/                    # integration tests against an in-memory DB
├── docs/                               # this directory
├── pom.xml
└── README.md
```

---

## Architecture overview

### Layered design

```
UI (Swing)
    |
    v
Service layer       <-- pure Java, no Swing imports, fully unit-testable
    |
    v
DAO layer           <-- JDBC; swappable between SQLite (prod) and H2 (tests)
    |
    v
SQLite / H2 database
```

The UI layer fires events (e.g. "user submitted a new transaction") and calls Service methods. Services validate input, apply business rules (e.g. check budget thresholds), and delegate persistence to the DAO layer. This separation makes it straightforward to add a REST API or CLI frontend in the future without touching business logic.

### Key design patterns

- **DAO pattern** — each entity (`Transaction`, `Budget`, `Category`) has a corresponding `*Dao` interface and a `Jdbc*Dao` implementation. Tests inject an H2 in-memory `Dao` instead.
- **Observer / event bus** — `BudgetService` fires `BudgetExceededEvent` when a transaction pushes spending past a configured threshold. UI panels register listeners to display warnings.
- **MVC for panels** — each Swing panel owns its own model (a plain Java object) and controller logic; data binding is manual but localised within each panel class.

---

## Running tests

```bash
# Run all unit + integration tests
mvn test

# Run only the service unit tests
mvn test -Dtest="*ServiceTest"

# Run only DAO integration tests (spins up H2)
mvn test -Dtest="*DaoTest"

# Generate a Surefire HTML report
mvn surefire-report:report
# Report is at target/site/surefire-report.html
```

### Test database

DAO tests use an **H2 in-memory database** that is created fresh for each test class via a `@BeforeAll` setup method. This keeps tests hermetic and fast (no SQLite file I/O).

---

## Configuration

Application settings live in `src/main/resources/config.properties`:

```properties
# Path to the SQLite database file (relative to the working directory)
db.path=budget.db

# Default currency symbol shown in the UI
currency.symbol=$

# Number of months shown in the spending trend chart
report.trend.months=6
```

Override any property at runtime with a system property:

```bash
mvn exec:java -Dexec.mainClass="com.budgetmanager.Main" \
              -Ddb.path=/tmp/test.db \
              -Dcurrency.symbol=€
```

---

## Building a fat JAR

```bash
mvn package -DskipTests
# Output: target/budget-manager-app-<version>-jar-with-dependencies.jar

java -jar target/budget-manager-app-*-jar-with-dependencies.jar
```

---

## Contributing

1. Fork the repository and create a feature branch: `git checkout -b feat/your-feature`.
2. Write tests first (TDD preferred); aim to keep line coverage above 80 % on the service layer.
3. Run `mvn checkstyle:check` before committing — the project uses Google Java Style.
4. Open a pull request against `main` with a clear description of the change and any relevant screenshots for UI changes.

### Checkstyle

The Google Style config is bundled in `checkstyle.xml`. Your IDE can import it:
- **IntelliJ**: Settings → Editor → Code Style → Java → Import Scheme → `checkstyle.xml`
- **Eclipse**: Properties → Checkstyle → New → External configuration file

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|-------------|-----|
| `ClassNotFoundException: org.sqlite.JDBC` | SQLite JDBC driver not on classpath | Run `mvn dependency:resolve` |
| Blank window on launch (macOS) | macOS requires Swing on the EDT with `-XstartOnFirstThread` on some JVMs | Add `-XstartOnFirstThread` to `MAVEN_OPTS` |
| `NullPointerException` in `DashboardPanel` | Database schema not initialised | Delete `budget.db` and relaunch to recreate schema |
| Tests fail with `Table not found` | H2 schema migration not applied | Check that `schema.sql` is on the test classpath under `src/test/resources` |
