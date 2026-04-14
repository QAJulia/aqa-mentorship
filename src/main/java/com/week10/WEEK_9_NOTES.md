# Week 9 – Unifying API & UI Framework

## Goals
- Combine API (REST Assured) and UI (Selenide) in one Maven project
- Introduce a clean three-layer base class hierarchy
- Centralise configuration in a single `AppConfig`
- Add structured logging with Logback
- Write cross-layer tests: fetch data from the API, verify it on the UI

---

## Part 1 – Why Unify?

In previous weeks we built separate Selenium/Selenide UI tests and (later) discussed API testing.
Real products have both, and bugs often appear at the boundary:

| Scenario | Caught by |
|---|---|
| API returns 8 books, UI shows only 7 | Unified test |
| API field name changes from `title` to `name` | API test |
| UI renders the wrong ISBN for a book | Unified test (API as source of truth) |

A unified framework lets you write **cross-layer tests** — tests that use the API
as a source of truth and the UI as the presentation layer to verify.

---

## Part 2 – Project Layout

```
src/
├── main/java/week09/
│   ├── config/
│   │   └── AppConfig.java              ← reads config.properties + system properties
│   ├── api/
│   │   ├── client/
│   │   │   └── BooksClient.java        ← REST Assured API client
│   │   └── model/
│   │       ├── Book.java               ← JSON → POJO
│   │       └── BooksResponse.java      ← top-level wrapper
│   ├── ui/
│   │   ├── base/
│   │   │   └── BasePage.java           ← shared Selenide helpers
│   │   └── pages/
│   │       ├── BookStorePage.java
│   │       └── BookDetailPage.java
│   └── utils/
│       ├── SelenideConfig.java         ← browser setup + AllureSelenide
│       └── RestAssuredConfig.java      ← Jackson mapper + logging
│
├── main/resources/
│   ├── config.properties               ← default environment values
│   └── logback.xml                     ← Logback configuration
│
└── test/java/
    ├── base.BaseTest.java                   ← root: logs environment
    ├── base.base.BaseApiTest.java                ← API base: REST Assured init + BooksClient
    ├── base.BaseUiTest.java                 ← UI base:  Selenide init + browser teardown
    ├── api/
    │   └── BooksApiTest.java           ← pure API tests
    ├── ui/
    │   └── BookStoreUiTest.java        ← pure UI tests
    └── unified/
        └── BookStoreSyncTest.java      ← cross-layer API + UI tests
```

---

## Part 3 – Base Class Hierarchy

```
base.BaseTest
  │  @BeforeSuite: log environment (URL, browser, headless)
  │
  ├── base.base.BaseApiTest
  │     @BeforeSuite: RestAssuredConfig.init()
  │     @BeforeMethod: new BooksClient()
  │     field: BooksClient booksClient
  │
  └── base.BaseUiTest
        @BeforeSuite: SelenideConfig.init()
        @AfterMethod: Selenide.closeWebDriver()
```

### Why three levels?

| Class | Knows about | Does NOT know about |
|---|---|---|
| `base.BaseTest` | Logging, AppConfig | Driver, HTTP client |
| `base.base.BaseApiTest` | REST Assured, BooksClient | Browser, Selenide |
| `base.BaseUiTest` | Selenide, browser lifecycle | HTTP, BooksClient |

A **unified test** (like `BookStoreSyncTest`) extends `base.BaseUiTest` and adds a
`BooksClient` field directly. It does NOT extend `base.base.BaseApiTest` — that would
inherit the REST Assured `@BeforeSuite` + `@BeforeMethod`, which is unnecessary
for a class that only needs the client object.

**Rule:** extend the base that matches your PRIMARY concern; add secondary concerns as fields.

---

## Part 4 – Configuration Layer

### config.properties (defaults)

```properties
ui.base.url=https://demoqa.com
ui.browser=chrome
ui.headless=false
ui.timeout=10000

api.base.url=https://demoqa.com
api.books.path=/BookStore/v1/Books
```

### AppConfig (typed accessor)

```java
AppConfig.getUiBaseUrl()   // → "https://demoqa.com"
AppConfig.isHeadless()     // → false (or true if -Dui.headless=true)
AppConfig.getBooksEndpoint() // → "https://demoqa.com/BookStore/v1/Books"
```

### Override from CLI (CI pipeline pattern)

```bash
mvn test -Dui.base.url=https://staging.demoqa.com \
         -Dui.headless=true \
         -Dui.timeout=15000
```

No code changes needed — `AppConfig` reads system properties first.

---

## Part 5 – API Layer

### REST Assured RequestSpecification

`BooksClient` builds a shared `RequestSpecification` once in its constructor:

```java
spec = new RequestSpecBuilder()
    .setBaseUri(AppConfig.getApiBaseUrl())
    .setContentType(ContentType.JSON)
    .addFilter(new AllureRestAssured())   // ← attaches req/resp to Allure
    .build();
```

The `AllureRestAssured` filter is the API equivalent of `AllureSelenide` — it
captures every request and response body and attaches them to the Allure report step.

### JSON → POJO with Jackson

```java
BooksResponse response = RestAssured.given(spec)
    .get("/BookStore/v1/Books")
    .then()
    .statusCode(200)
    .extract()
    .as(BooksResponse.class);   // ← Jackson deserialises automatically

List<Book> books = response.getBooks();
```

`BooksResponse` is annotated with `@JsonIgnoreProperties(ignoreUnknown = true)` —
the API can add fields without breaking tests.

---

## Part 6 – Logging with Logback

### Why Logback instead of slf4j-simple?

| Feature | slf4j-simple | Logback |
|---|---|---|
| Per-package log levels | No | Yes |
| File output | No | Yes (rolling) |
| Pattern customisation | No | Yes |
| Allure integration | Indirect | Full |

### logback.xml key settings

```xml
<!-- Our code: DEBUG (verbose) -->
<logger name="org.example" level="DEBUG"/>

<!-- Suppress Selenide / WebDriver noise -->
<logger name="com.codeborne.selenide" level="WARN"/>
<logger name="org.openqa.selenium"    level="WARN"/>

<!-- REST Assured: suppress verbose request details in console -->
<logger name="io.restassured"         level="WARN"/>
```

### Usage in code

```java
private final Logger log = LoggerFactory.getLogger(getClass());

log.debug("BooksClient initialised: {}", AppConfig.getApiBaseUrl());
log.info("API returned {} books", books.size());
log.warn("Unexpected response: {}", response.statusCode());
```

---

## Part 7 – Cross-Layer Testing Pattern

### "API-first verification"

```
Step 1  →  API: GET /Books              Source of truth (fast)
Step 2  →  UI: open /books page
Step 3  →  Assert: all API titles visible on UI
```

```java
// In BookStoreSyncTest:

// 1. API call
List<Book> apiBooks = booksClient.getAllBooks();

// 2. UI open
BookStorePage uiPage = new BookStorePage().open().isPageOpened();
List<String> uiTitles = uiPage.getAllBookTitles();

// 3. Cross-assert
for (String apiTitle : apiTitles) {
    assertTrue(uiTitles.contains(apiTitle),
            "API title not found on UI: " + apiTitle);
}
```

### Benefits

- **Speed**: if the API is down, the test fails at step 1 — no browser needed
- **Reliability**: you're not guessing what the UI "should" show; the API tells you
- **Coverage**: catches mismatches that pure UI or pure API tests cannot see

---

## Part 8 – Allure Report Integration

### Three Allure listeners, each for a different layer

| Listener | Registered in | What it captures |
|---|---|---|
| `AllureTestNg` | `testng-week9.xml` | Test start/stop/fail/skip events |
| `AllureSelenide` | `SelenideConfig.init()` | Browser screenshots on failure |
| `AllureRestAssured` | `BooksClient` constructor | HTTP request + response body |

In the Allure report you will see:
- For API tests: full request URL, headers, response body (JSON)
- For UI tests:  Selenide step descriptions + screenshot on failure
- For unified tests: both, in the correct order

---

## Part 9 – pom.xml New Dependencies

```xml
<!-- REST Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
</dependency>

<!-- Jackson – JSON ↔ POJO -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>

<!-- Allure REST Assured (replaces @Step annotations in API client) -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-rest-assured</artifactId>
    <version>2.25.0</version>
</dependency>

<!-- Logback (replaces slf4j-simple) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.11</version>
</dependency>
```

---

## Running

```bash
# Full suite
mvn clean test

# Headless CI mode
mvn clean test -Dui.headless=true

# Different environment
mvn clean test -Dui.base.url=https://staging.demoqa.com \
               -Dapi.base.url=https://staging.demoqa.com

# Allure report
mvn allure:serve
```
