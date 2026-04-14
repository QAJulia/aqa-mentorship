package com.week10.api.spec;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * BookSchemaValidator – centralises JSON Schema validation for BookStore endpoints.
 *
 * JSON SCHEMA VALIDATION IN REST ASSURED:
 *   REST Assured integrates with the json-schema-validator library via the
 *   static import:
 *       import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
 *
 *   Usage:
 *       response.then().body(matchesJsonSchemaInClasspath("schemas/books-list-schema.json"));
 *
 *   matchesJsonSchemaInClasspath() reads the schema file from the classpath
 *   (src/main/resources/schemas/ is on the classpath after mvn compile).
 *
 * WHY A DEDICATED VALIDATOR CLASS?
 *   - Schema file paths are strings — typos cause silent failures if not centralised
 *   - Validation calls become @Step methods → visible in Allure report
 *   - Tests call assertBooksListSchema(response) — no raw string literals
 *
 * SCHEMA FILES (in src/main/resources/schemas/):
 *   books-list-schema.json   → validates GET /BookStore/v1/Books
 *   book-single-schema.json  → validates a single Book object
 */
public final class BookSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(BookSchemaValidator.class);

    private static final String BOOKS_LIST_SCHEMA  = "schemas/books-list-schema.json";
    private static final String BOOK_SINGLE_SCHEMA = "schemas/book-single-schema.json";

    private BookSchemaValidator() {}

    /**
     * Validates that the response body conforms to the books-list schema.
     * Asserts:
     *   - Top-level "books" array exists
     *   - Each book has required fields: isbn, title, author, pages, etc.
     *   - Field types are correct (pages is integer, not string)
     *   - No unexpected additional properties
     *
     * @param response the raw REST Assured Response to validate
     */
    @Step("Validate response against books-list JSON schema")
    public static void assertBooksListSchema(Response response) {
        log.debug("Validating response against schema: {}", BOOKS_LIST_SCHEMA);
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(BOOKS_LIST_SCHEMA));
        log.info("Books list schema validation PASSED");
    }

    /**
     * Validates that the response body conforms to the single-book schema.
     * Used for GET /BookStore/v1/Book?ISBN={isbn} responses.
     *
     * @param response the raw REST Assured Response to validate
     */
    @Step("Validate response against book-single JSON schema")
    public static void assertBookSingleSchema(Response response) {
        log.debug("Validating response against schema: {}", BOOK_SINGLE_SCHEMA);
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(BOOK_SINGLE_SCHEMA));
        log.info("Single book schema validation PASSED");
    }
}
