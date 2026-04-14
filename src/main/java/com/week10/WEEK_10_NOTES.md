# Week 10 – Advanced REST Assured & API Design

## Overview

This week elevates the REST Assured framework from Week 9 into a production-grade API testing
architecture. The key themes are:

1. **Spec Factories** – centralise `RequestSpecification` and `ResponseSpecification` so every
   test method reuses the same building blocks.
2. **JSON Schema Validation** – assert the *shape* of a response, not just individual values.
3. **Custom Filters** – cross-cutting concerns (timing, correlation IDs, logging) plugged in via
   the REST Assured `Filter` interface.
4. **Service-Layer Pattern** – `BooksClient` is the only class that touches `given()`. Tests speak
   in domain terms (`client.getAllBooks()`).
5. **Allure Categorisation** – distinguish product bugs, test bugs, and infrastructure failures
   automatically.

---

## 1. RequestSpecification & ResponseSpecification

### Why Shared Specs?

Without specs, every test repeats the same boilerplate:

```java
// BEFORE – repeated in every test
given()
    .baseUri("https://demoqa.com")
    .basePath("/BookStore/v1")
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
.when()
    .get("/Books")
.then()
    .statusCode(200)
    .contentType(ContentType.JSON);
```

With factories, the test becomes one line and the spec lives in one place:

```java
// AFTER – clean, DRY, maintainable
given(RequestSpecFactory.defaultSpec())
    .when().get("/Books")
    .then().spec(ResponseSpecFactory.ok200WithJson());
```

### RequestSpecFactory

```java
public class RequestSpecFactory {

    /** Full spec – base URI, content-type, accept, custom filters. */
    public static RequestSpecification defaultSpec() {
        return new RequestSpecBuilder()
            .setBaseUri(AppConfig.get().getBaseUrl())   // from config.properties
            .setBasePath("/BookStore/v1")
            .setContentType(ContentType.JSON)
            .addHeader("Accept", "application/json")
            .addFilter(new CorrelationIdFilter())        // adds X-Correlation-ID
            .addFilter(new TimingFilter())               // measures + Allure attachment
            .addFilter(new AllureRestAssured())          // Allure request/response log
            .log(LogDetail.URI)
            .build();
    }

    /** Spec with a Bearer token injected. */
    public static RequestSpecification authenticatedSpec(String token) {
        return new RequestSpecBuilder()
            .addRequestSpecification(defaultSpec())
            .addHeader("Authorization", "Bearer " + token)
            .build();
    }

    /** Minimal spec – no filters, no Allure – useful for unit-level spec tests. */
    public static RequestSpecification minimalSpec() {
        return new RequestSpecBuilder()
            .setBaseUri(AppConfig.get().getBaseUrl())
            .setBasePath("/BookStore/v1")
            .build();
    }
}
```

**Key API:**
| Method | Purpose |
|--------|---------|
| `RequestSpecBuilder` | Fluent builder for `RequestSpecification` |
| `.addRequestSpecification(base)` | Merge an existing spec (inheritance) |
| `.build()` | Returns the immutable `RequestSpecification` |

### ResponseSpecFactory

```java
public class ResponseSpecFactory {

    public static ResponseSpecification ok200WithJson() {
        return new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .build();
    }

    public static ResponseSpecification ok200WithJsonFast() {
        return new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectResponseTime(lessThan(3000L), TimeUnit.MILLISECONDS)
            .build();
    }

    public static ResponseSpecification badRequest400() {
        return new ResponseSpecBuilder()
            .expectStatusCode(400)
            .build();
    }
}
```

**Key rule:** `ResponseSpecification` assertions are additive — you can compose specs:

```java
.then()
    .spec(ResponseSpecFactory.ok200WithJson())   // status + content-type
    .body("books", not(empty()));               // additional field assertion
```

---

## 2. JSON Schema Validation

### What Is JSON Schema?

JSON Schema (Draft-07 used here) is a declarative vocabulary for describing the structure of a
JSON document. It can assert:

- Required properties (`"required": ["isbn", "title"]`)
- Property types (`"type": "string"`)
- Array constraints (`"minItems": 1`)
- Nested object shapes

### Why Schema Tests?

| Test type | What it catches |
|-----------|----------------|
| Field-value test | Wrong value in a known field |
| Schema test | Missing field, wrong type, structural regression |

A schema test acts as a **contract test** — it verifies the API still fulfils the agreed shape
even after backend changes.

### Schema Files (classpath: `schemas/`)

**`books-list-schema.json`** — asserts the `GET /Books` response:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["books"],
  "properties": {
    "books": {
      "type": "array",
      "minItems": 1,
      "items": {
        "type": "object",
        "required": ["isbn", "title", "subTitle", "author",
                     "publish_date", "publisher", "pages",
                     "description", "website"],
        "properties": {
          "isbn":         { "type": "string" },
          "title":        { "type": "string" },
          "subTitle":     { "type": "string" },
          "author":       { "type": "string" },
          "publish_date": { "type": "string" },
          "publisher":    { "type": "string" },
          "pages":        { "type": "integer", "minimum": 1 },
          "description":  { "type": "string" },
          "website":      { "type": "string", "format": "uri" }
        }
      }
    }
  }
}
```

**`book-single-schema.json`** — asserts a single `GET /Book?ISBN=` response (same properties,
minus the wrapping `books` array).

### Using Schema Validation

REST Assured integrates with the `json-schema-validator` library via a Hamcrest matcher:

```java
// Dependency required (added to pom.xml):
// io.rest-assured:json-schema-validator:5.4.0

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

response.then()
    .body(matchesJsonSchemaInClasspath("schemas/books-list-schema.json"));
```

The `BookSchemaValidator` utility class wraps these assertions as Allure `@Step` methods so they
appear as named steps in the report:

```java
@Step("Validate books list JSON schema")
public void assertBooksListSchema(ValidatableResponse response) {
    response.body(matchesJsonSchemaInClasspath("schemas/books-list-schema.json"));
}
```

### pom.xml Change

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

---

## 3. Custom Filters

### The Filter Interface

```java
public interface Filter {
    Response filter(FilterableRequestSpecification requestSpec,
                    FilterableResponseSpecification responseSpec,
                    FilterContext ctx);
}
```

Filters sit **between** your test code and the HTTP call. They can:
- Mutate the request before it is sent (`requestSpec.header(...)`)
- Measure timing around `ctx.next(requestSpec, responseSpec)`
- Attach data to Allure or a log

### TimingFilter

Measures wall-clock time and attaches it to the Allure report as a text attachment:

```java
public class TimingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        long start = System.currentTimeMillis();
        Response response = ctx.next(req, res);           // → actual HTTP call
        long elapsed = System.currentTimeMillis() - start;

        String summary = String.format("Method: %s  URI: %s  Status: %d  Time: %dms",
            req.getMethod(), req.getURI(), response.getStatusCode(), elapsed);
        Allure.addAttachment("Response Timing", "text/plain", summary);
        return response;
    }
}
```

**Pattern:** always call `ctx.next(...)` and return its result. Modifying the `Response` object
returned by `ctx.next()` is possible but rare.

### CorrelationIdFilter

Injects a `X-Correlation-ID` UUID header. In real projects this enables distributed tracing
across microservices:

```java
public class CorrelationIdFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        String correlationId = UUID.randomUUID().toString();
        req.header("X-Correlation-ID", correlationId);
        Allure.addAttachment("X-Correlation-ID", correlationId);
        return ctx.next(req, res);
    }
}
```

### Filter Registration

Filters are added once in `RequestSpecFactory.defaultSpec()` — all clients that use the default
spec automatically get timing and correlation ID tracking without any extra code in tests.

---

## 4. Service-Layer Pattern (BooksClient)

### Layered Architecture

```
Test class
    └── BooksClient              ← service layer (API domain language)
            └── RequestSpecFactory / ResponseSpecFactory   ← spec layer
                    └── REST Assured core (given/when/then)
```

### BooksClient Design

```java
public class BooksClient {

    private final RequestSpecification spec;

    public BooksClient() {
        this.spec = RequestSpecFactory.defaultSpec();
    }

    // --- Raw response methods (used by schema/spec tests) ---

    public ValidatableResponse getBooksRawResponse() {
        return given(spec)
            .when().get("/Books")
            .then().spec(ResponseSpecFactory.ok200WithJson());
    }

    public ValidatableResponse getBookByIsbnRawResponse(String isbn) {
        return given(spec)
            .queryParam("ISBN", isbn)
            .when().get("/Book")
            .then().spec(ResponseSpecFactory.ok200WithJson());
    }

    // --- Domain-typed methods (used by functional tests) ---

    public BooksResponse getAllBooks() {
        return getBooksRawResponse()
            .extract().as(BooksResponse.class);
    }

    public Book getBookByIsbn(String isbn) {
        return getBookByIsbnRawResponse(isbn)
            .extract().as(Book.class);
    }

    public Book getFirstBook() {
        List<Book> books = getAllBooks().getBooks();
        assertThat(books).as("Book list must not be empty").isNotEmpty();
        return books.get(0);
    }

    // --- Spec variation factory ---

    public static BooksClient withMinimalSpec() {
        return new BooksClient(RequestSpecFactory.minimalSpec());
    }
}
```

**Rules:**
- Tests NEVER call `given()` directly
- Tests receive either a `ValidatableResponse` (for assertion chaining) or a typed domain object
- The `withMinimalSpec()` factory demonstrates how to swap specs for specific scenarios

---

## 5. Allure Categorisation

### allure-categories.json

Placed in `src/test/resources/` and referenced during report generation. It classifies test
failures automatically:

```json
[
  {
    "name": "Product Defects",
    "matchedStatuses": ["failed"],
    "messageRegex": ".*AssertionError.*|.*expected:.*but was:.*"
  },
  {
    "name": "Test Defects",
    "matchedStatuses": ["broken"],
    "messageRegex": ".*NullPointerException.*|.*IllegalStateException.*"
  },
  {
    "name": "Infrastructure Problems",
    "matchedStatuses": ["broken"],
    "messageRegex": ".*Connection refused.*|.*SocketTimeoutException.*|.*UnknownHostException.*"
  }
]
```

| Category | Status | Meaning |
|----------|--------|---------|
| Product Defects | `failed` | Assertion failed — the API returned wrong data |
| Test Defects | `broken` | Test threw a runtime exception — test code is broken |
| Infrastructure Problems | `broken` | Cannot connect — environment issue |

### Allure Tag Hierarchy Used in Week 10

```
@Epic("BookStore API")           ← top-level product area
  @Feature("Books Endpoint")     ← specific API resource
    @Story("Get All Books")      ← concrete user story / scenario

@Severity(SeverityLevel.CRITICAL)
@Owner("mentor")
@TmsLink("TC-10-001")            ← link to test management system
@Link(url="...", name="API Docs")
@Description("Plain-text description shown in Allure UI")
```

### testng-week10.xml Listener

```xml
<listeners>
    <listener class-name="io.qameta.allure.testng.AllureTestNg"/>
</listeners>
```

This is required for Allure to intercept TestNG lifecycle events. Without it, `@Epic`, `@Feature`,
`@Story` annotations are ignored.

---

## 6. pom.xml Changes vs Week 9

| What changed | Why |
|---|---|
| Added `json-schema-validator:5.4.0` | Enables `matchesJsonSchemaInClasspath()` |
| `allure-rest-assured:2.25.0` already present | Provides `AllureRestAssured` filter |
| No other changes | `rest-assured:5.4.0`, `jackson-databind`, `allure-testng`, `testng` all inherited |

Full diff in pom.xml (search for `json-schema-validator`).

---

## 7. Test Structure Overview

```
src/test/java/
└── lesson10/
    ├── base.BaseApiTest.java            ← @BeforeSuite sets RestAssured defaults
    │                                  @BeforeMethod creates BooksClient
    ├── api/
    │   └── BooksApiTest.java       ← smoke + regression functional tests
    ├── schema/
    │   └── SchemaValidationTest.java ← schema/contract tests
    └── spec/
        └── RequestSpecTest.java    ← spec-reuse demonstration tests

src/test/resources/
├── testng-week10.xml
├── allure-categories.json
└── schemas/
    ├── books-list-schema.json
    └── book-single-schema.json     (also in src/main/resources/schemas/ for classpath)
```

### Test Groups Matrix

| Class | Groups | Purpose |
|---|---|---|
| `BooksApiTest` | `smoke`, `regression`, `api`, `contract`, `performance` | Functional HTTP contract |
| `SchemaValidationTest` | `regression`, `api`, `schema`, `contract` | JSON schema assertions |
| `RequestSpecTest` | `regression`, `spec` | Spec factory reuse demos |

---

## 8. Running the Tests

```bash
# Run all API tests
mvn test -Dtestng.suiteXmlFiles=src/test/resources/testng-week10.xml

# Generate Allure report
mvn allure:serve
```

---

## 9. Key Takeaways

1. **Never repeat request boilerplate** — extract it once into a `RequestSpecFactory`.
2. **Schema tests are contract tests** — they catch structural regressions that value tests miss.
3. **Filters are the right place** for cross-cutting concerns: timing, auth headers, logging.
4. **Allure tags tell a story** — `@Epic → @Feature → @Story` maps directly onto the product
   backlog hierarchy.
5. **Categories turn red builds into actionable signals** — engineers know immediately whether a
   failure is a product bug, a broken test, or a dead environment.
