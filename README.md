# Java AQA Mentorship Program – 3‑Month Plan

Calls are scheduled **once per week**. Each week consists of:
- 1 hour live session (theory + live coding)
- Homework with code review
- Gradual evolution of a single automation framework (API + UI)

---

## Month 1 – TestNG, Reporting, API Basics

### Week 1 – TestNG Basics

**Goals**
- Understand Maven project structure
- Learn TestNG basics and lifecycle hooks
- Run tests from the command line

**Topics**
- Maven project layout: `src/main/java`, `src/test/java`
- TestNG annotations: `@Test`, `@BeforeMethod`, `@AfterMethod`, `@BeforeClass`, `@AfterClass`
- DataProviders for parameterized tests
- Assertions and the AAA (Arrange–Act–Assert) pattern
- Running tests via Maven: `mvn test`, `mvn -Dtest=ClassName test`

**Deliverables**
- `StringValidator` class with basic methods
- `StringValidatorTest` with DataProviders and all key TestNG hooks
- `NOTES.md` and `TASKS.md` documenting a class and homework

---

### Week 2 – testng.xml, Parameters, External Data, Lombok

**Goals**
- Configure and run tests using `testng.xml`
- Use parameters from XML
- Provide test data from external sources (CSV/JSON)

**Topics**
- `testng.xml` structure: `<suite>`, `<test>`, `<classes>`, `<methods>`, `<groups>`, `parallel`, `thread-count`
- Running suites:
    - From IDE using `testng.xml`
    - From CLI: `mvn test -DsuiteXmlFile=testng.xml`
- `@Parameters` and `@Optional`
- `@Parameters` vs `@DataProvider`
- Reading test data from CSV/JSON:
    - Utility class for file reading
    - DataProvider that reads from CSV/JSON

**Deliverables**
- Two XML suites: `smoke` and `regression`
- Tests reading URL/browser from XML parameters
- DataProvider that reads test cases from CSV (or JSON) file

---

### Week 3 – Allure Reporting & TestNG Listeners

**Goals**
- Integrate Allure reporting into the project
- Use TestNG listeners to hook into test lifecycle
- Enrich Allure reports with metadata and custom logs

**Topics**
- Adding Allure dependencies and Maven Surefire configuration
- Allure annotations: `@Epic`, `@Feature`, `@Story`, `@Severity`, `@Description`, `@Step`
- Generating reports: `mvn allure:serve`
- TestNG listeners: `ITestListener`, `ISuiteListener`
- Registering listeners:
    - Via `@Listeners`
    - Via `testng.xml`
- Logging test start/finish, capturing failures, attaching extra info to Allure

**Deliverables**
- `listeners` package with custom TestNG listeners
- All tests enriched with Allure annotations
- Custom listener logging failed tests and durations, visible in Allure reports

---

### Week 4 – API Testing: REST & SOAP, POJOs

**Goals**
- Write API tests using Rest Assured
- Model request/response bodies with POJOs
- Use Lombok, Jackson/Gson for serialization/deserialization
- Execute at least one SOAP request

**Topics**
- HTTP basics: methods, status codes, headers, body
- Rest Assured:
    - `given–when–then` syntax
    - Path/query parameters
    - Validating status codes and response body
- POJOs for JSON models
- Lombok annotations: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Jackson/Gson for JSON ↔ POJO mapping
- Simple SOAP example using Rest Assured with raw XML body

**Deliverables**
- `api` package with:
    - GET (list + by id) tests
    - POST test using POJO as request body
    - Negative test (e.g., 404/400)
- One simple SOAP test
- All API tests integrated with Allure and runnable via `testng.xml`

---

## Month 2 – Selenium, XPath, Page Object, UI Patterns

### Week 5 – Selenium WebDriver Basics

**Goals**
- Launch and control browsers with Selenium WebDriver
- Implement simple UI scenarios

**Topics**
- WebDriver setup (e.g., ChromeDriver, WebDriverManager)
- Navigation, basic waits (implicit and introduction to explicit waits)
- Locators: id, name, className, cssSelector, basic xpath
- Simple end‑to‑end scenario on a demo web app
- Basic `BaseUiTest` abstraction

**Deliverables**
- WebDriver setup controlled via configuration (URL, browser)
- 2–3 basic UI scenarios automated as TestNG tests

---

### Week 6 – Advanced XPath & Basic Page Object

**Goals**
- Confidently write complex XPath expressions
- Introduce the Page Object pattern

**Topics**
- Advanced XPath:
    - Axes: `following-sibling`, `ancestor`, `preceding`
    - `contains`, `starts-with`, text‑based selectors
    - Working with tables, lists, nested structures
- Page Object pattern:
    - Separation of concerns: pages vs tests
    - Page classes containing locators + actions
    - Tests using page methods instead of raw WebDriver calls

**Deliverables**
- Existing tests refactored to Page Object style
- At least 2–3 page classes with advanced XPath locators
- WebDriver logic moved from tests into base/page classes

---

### Week 7 – UI Test Architecture & Patterns

**Goals**
- Learn common UI test design patterns
- Avoid typical anti‑patterns in automation code

**Topics**
- Patterns:
    - Classic Page Object vs Page Factory (pros/cons)
    - Fluent Page Object (methods returning `this` or next page)
    - Loadable Component / `isPageOpened` checks
    - Screen/State objects for complex UIs
- Separation of layers:
    - Tests vs pages vs services vs configuration
    - Test data management (builders, object factories)
- Anti‑patterns:
    - Business logic inside test methods
    - Duplicated locators
    - Hardcoded `Thread.sleep`

**Deliverables**
- 1–2 key scenarios rewritten using Fluent Page Object style
- `isPageOpened()` (or similar) checks on key pages
- Test data moved into dedicated builders/factories

---

### Week 8 – Selenide Introduction

**Goals**
- Simplify Selenium code using Selenide
- Use Selenide’s fluent API and built‑in waits

**Topics**
- Adding Selenide dependency and configuration
- Selenide basics:
    - `open`, `$(selector)`, `$$`
    - Conditions: `shouldBe`, `shouldHave`, timeouts
- Configuration: browser, timeout, screenshots
- Migrating existing Selenium tests to Selenide
- Using `SelenideElement` in Page Objects

**Deliverables**
- 2–3 key UI tests migrated from pure Selenium to Selenide
- Page Objects updated to use Selenide where appropriate
- Allure still integrated with Selenide tests (screenshots, steps)

---

## Month 3 – Consolidation, Framework Hardening, Final Project

### Week 9 – Unifying API & UI Framework

**Goals**
- Have a coherent project structure combining API and UI
- Introduce common base classes and utilities

**Topics**
- Final project layout for API + UI modules/packages
- Base classes:
    - `BaseTest`, `BaseApiTest`, `BaseUiTest`
- Shared utilities:
    - Configuration management (properties/typed config)
    - Logging setup

**Deliverables**
- Unified project structure
- Common base classes used across tests
- Updated `README.md` and notes to match actual architecture

---

### Week 10 – Advanced Rest Assured & API Design

**Goals**
- Make API tests more robust and maintainable
- Introduce reusable API client layer

**Topics**
- Request and response specifications in Rest Assured
- JSON schema validation
- Logging and filters in Rest Assured
- API client/service layer pattern (wrapping Rest Assured calls)

**Deliverables**
- API client classes encapsulating Rest Assured calls
- JSON schema validation for 1–2 key endpoints
- Allure categories/tags distinguishing API tests

---

### Week 11 – Framework Polishing & CI Introduction

**Goals**
- Prepare the framework for CI usage
- Introduce grouping and parallel execution

**Topics**
- TestNG parallel execution (`parallel`, `thread-count` in XML)
- Test groups: `smoke`, `regression`, `api`, `ui`, etc.
- CI basics (e.g., GitHub Actions / GitLab CI):
    - Pipeline steps: checkout → build → test → publish Allure report artifacts

**Deliverables**
- `@Test(groups = "...")` groups on key tests
- Maven commands or profiles to run specific suites/groups
- Initial CI pipeline (even minimal) to run tests and collect Allure results

---

### Week 12 – Final Project & Review

**Goals**
- Demonstrate a complete automation framework
- Perform architecture and code review
- Identify next growth areas

**Activities**
- Mentee demo:
    - Project structure explanation
    - Example UI scenario
    - Example API scenario
    - Allure report walkthrough
- Joint review:
    - Code quality and architecture
    - What to refactor
    - Ideas for future improvements (BDD, DB checks, Dockerized env, etc.)

**Final Assignment**
- Implement an end‑to‑end flow:
    - Prepare data via API
    - Execute UI steps via Selenide
    - Validate via API and/or database (if available)


