# Week 11 – Framework Polishing & CI Introduction

## Overview

This week takes the Week 10 API framework and prepares it for real-world use in a team
environment. The two main themes are:

1. **Parallel execution** – run independent API tests concurrently to cut suite time.
2. **CI/CD integration** – a GitHub Actions pipeline that runs tests on every push,
   collects Allure results, and makes them downloadable as artifacts.

No new test logic is added — the improvement is purely structural and operational.

---

## 1. TestNG Parallel Execution

### How TestNG parallelism works

TestNG's parallel mode is declared in the suite XML, not in Java code:

```xml
<suite name="My Suite" parallel="methods" thread-count="3">
```

| `parallel=` value | What runs in parallel |
|-------------------|-----------------------|
| `methods`         | Each `@Test` method gets its own thread — maximum concurrency |
| `classes`         | Methods in a class share one thread, classes run in parallel |
| `tests`           | Each `<test>` block runs in its own thread |
| `instances`       | Separate instances of a class (useful with `@Factory`) |

**We use `parallel="methods"`** because:
- All tests are pure REST API calls — no shared browser state.
- `BooksClient` is constructed fresh per `@BeforeMethod` so there is no
  mutable shared state between test methods.

### thread-count guidance

| Suite | thread-count | Reasoning |
|-------|-------------|-----------|
| Smoke | 2 | Fast enough; low server load |
| Parallel (default) | 3 | Balanced: 14 methods / 3 ≈ 5 rounds |
| Regression | 4 | Throughput on nightly run |

Avoid going above 5 on DemoQA — the free tier can return `503` under load.

### Thread safety rules

When running in parallel, every piece of shared mutable state is a potential
race condition. Our framework follows these rules:

**Safe — read-only after init:**
```java
// @BeforeSuite runs BEFORE the parallel phase — safe to write once
@BeforeSuite
public void initRestAssured() {
    RestAssured.baseURI = AppConfig.getApiBaseUrl();  // written once, then read-only
}
```

**Safe — per-thread instances via @BeforeMethod:**
```java
@BeforeMethod
public void createClient() {
    booksClient = new BooksClient();  // each thread gets its own BooksClient
}
```

**Extra safety — ThreadLocal in BaseApiTest:**
```java
private final ThreadLocal<BooksClient> clientHolder = new ThreadLocal<>();

@BeforeMethod
public void createClient() {
    BooksClient client = new BooksClient();
    clientHolder.set(client);   // stored per-thread
    booksClient = client;       // field alias for convenience
}
```

**Avoid — static mutable state:**
```java
// WRONG – all threads share this, causes race conditions in parallel:
public class BaseApiTest {
    protected static BooksClient booksClient;   // ← NEVER static in parallel tests
}
```

### Suite files summary

| File | Parallel | Threads | Groups |
|------|----------|---------|--------|
| `testng-week11-parallel.xml` | methods | 3 | all |
| `testng-week11-smoke.xml`    | methods | 2 | smoke |
| `testng-week11-regression.xml` | methods | 4 | all groups |

---

## 2. Test Groups Strategy

Groups let you slice the suite without modifying test code.

### Group hierarchy used in this project

```
smoke        → fast sanity gate (runs on every push, ~30 s)
  └── contract    → HTTP-level assertions (status code, content type)
  └── schema      → JSON schema validation

regression   → full validation (runs on PR / nightly)
  └── api         → all REST API tests
  └── model       → domain model field validations
  └── books-list  → list endpoint
  └── single-book → single-book endpoint
  └── performance → response time assertions
  └── spec        → RequestSpec / ResponseSpec pattern demos
```

### Running a specific group from the command line

```bash
# Only smoke
mvn test -f week11/pom.xml -Dsuite=testng-week11-smoke.xml

# Only performance tests
mvn test -f week11/pom.xml -Dgroups=performance

# Using a Maven profile
mvn test -f week11/pom.xml -Psmoke
mvn test -f week11/pom.xml -Pregression
mvn test -f week11/pom.xml -Pparallel   # default suite
mvn test -f week11/pom.xml -Pci         # smoke + headless flag
```

### Maven Profiles vs -Dsuite

Both achieve the same result — picking a suite file. Profiles are preferred for CI
because they can bundle multiple configuration changes (suite file + system properties)
under a single flag:

```xml
<profile>
    <id>ci</id>
    <properties>
        <suite>testng-week11-smoke.xml</suite>
    </properties>
    <build>
        <!-- Injects -Dui.headless=true for the CI environment -->
    </build>
</profile>
```

---

## 3. GitHub Actions CI Pipeline

### Concepts

**GitHub Actions** is a free CI/CD platform built into GitHub. Workflows are defined as
YAML files inside `.github/workflows/`.

| Concept | Description |
|---------|-------------|
| **workflow** | The top-level YAML file — one workflow per concern (ci, release, etc.) |
| **trigger** | The `on:` key — when the workflow runs (push, pull_request, schedule, manual) |
| **job** | A logical unit of work that runs on a specific machine (runner) |
| **step** | A single command or action within a job |
| **action** | A reusable unit published to the GitHub Marketplace (e.g. `actions/checkout`) |
| **runner** | The virtual machine that executes the job (`ubuntu-latest` = free) |
| **artifact** | A file uploaded from a job that can be downloaded after the run |

### Our pipeline structure

```
on: push → week11   or   PR → main   or   manual

jobs:
  smoke  (every push)
    ├── checkout
    ├── setup-java 11 + cache Maven
    ├── mvn test -Pci
    ├── mvn allure:report
    ├── upload allure-results artifact
    └── upload allure-report artifact

  regression  (PR only, runs after smoke passes)
    ├── checkout
    ├── setup-java 11 + cache Maven
    ├── mvn test -Pregression
    ├── mvn allure:report
    ├── upload allure-results-regression
    └── upload allure-report-regression
```

### Pipeline triggers explained

```yaml
on:
  push:
    branches:
      - week11          # every commit on this branch triggers the smoke job
  pull_request:
    branches:
      - main            # opening/updating a PR to main triggers smoke + regression
  workflow_dispatch:    # "Run workflow" button in GitHub Actions UI
```

### Maven cache explained

```yaml
- uses: actions/setup-java@v4
  with:
    java-version: '11'
    distribution: 'temurin'
    cache: 'maven'
```

Without caching, every CI run downloads ~150 MB of Maven dependencies.
With `cache: 'maven'`, the runner caches `~/.m2/repository` keyed on the
hash of all `pom.xml` files. On a cache hit (no pom changes) the download
is skipped entirely — saving 30–60 seconds per run.

### `if: always()` on report steps

```yaml
- name: Generate Allure report
  if: always()
  run: mvn allure:report
```

Without `if: always()`, steps are skipped if a previous step failed.
We want the Allure report even when tests fail — that's exactly when
you need it most to diagnose the failure.

### needs: — job dependencies

```yaml
regression:
  needs: smoke      # regression only starts if smoke PASSED
  if: github.event_name == 'pull_request'
```

`needs:` creates an explicit dependency graph. If smoke fails, regression is
automatically skipped, saving runner minutes on a broken build.

### Downloading the Allure report

After a pipeline run completes:
1. Go to the GitHub Actions run page
2. Scroll to **Artifacts** at the bottom
3. Download `allure-report`
4. Unzip → open `index.html` in a browser

For a live hosted report, consider [Allure TestOps](https://qameta.io/) or
deploy the static HTML to GitHub Pages.

---

## 4. Git Branch Convention

All Week 11 changes live on the `week11` branch:

```bash
# Create the branch locally
git checkout -b week11

# Add all week11 files
git add week11/
git commit -m "feat: week11 – parallel execution & GitHub Actions CI"

# Push to GitHub (triggers the CI workflow)
git push -u origin week11

# When ready to merge
git checkout main
git merge week11
```

The `.github/workflows/ci.yml` must be on the branch that GitHub reads workflows from.
For personal repos, GitHub reads workflows from the branch being pushed.
For PRs, GitHub reads the workflow from the target branch (main), so the workflow
file must also exist on main before PRs can trigger it.

**Recommended setup order:**
1. Push `week11` branch with all files including `.github/workflows/ci.yml`
2. Open a PR from `week11` → `main`
3. Merge the PR (this puts the workflow on `main`)
4. Future PRs will now auto-trigger both smoke and regression

---

## 5. pom.xml Changes vs Week 10

| Change | Why |
|--------|-----|
| `artifactId` → `java-aqa-mentorship-week11` | Distinguish from week10 artifact |
| `<suite>` property with default value | Lets `mvn test -Dsuite=...` swap suites |
| `forkCount=1 reuseForks=true` (explicit) | Single JVM, threads inside — safe for our setup |
| `<profiles>` section added | `smoke`, `regression`, `parallel`, `ci` profiles |
| `surefire.version` extracted to property | Avoid version drift between plugin and dependency |

---

## 6. Full Command Reference

```bash
# Run with default suite (parallel, all tests)
mvn test -f week11/pom.xml

# Named profiles
mvn test -f week11/pom.xml -Psmoke
mvn test -f week11/pom.xml -Pregression
mvn test -f week11/pom.xml -Pci

# Override suite inline
mvn test -f week11/pom.xml -Dsuite=testng-week11-smoke.xml

# Run a specific group (Surefire passes -Dgroups to TestNG)
mvn test -f week11/pom.xml -Dgroups=schema

# Generate and open Allure report locally
mvn allure:serve -f week11/pom.xml

# Generate report without serving
mvn allure:report -f week11/pom.xml
# Report output: week11/target/site/allure-maven-plugin/index.html
```

---

## 7. Key Takeaways

1. **`parallel="methods"`** is the right choice for stateless API tests — maximum concurrency
   with minimal risk of interference.
2. **Thread safety requires discipline**: `@BeforeMethod` creates a new client per thread;
   `@BeforeSuite` only writes static state once before parallelism starts.
3. **Profiles bundle context**: `-Pci` means "smoke suite + headless" — one flag instead of
   two `-D` properties.
4. **`if: always()`** on report steps ensures you can debug failures even when the build is red.
5. **`needs:`** prevents wasting CI minutes: regression only runs if smoke passes.
6. **Maven caching** is the single easiest CI speedup — always enable it.
