package com.week10.api.client;

import com.week10.api.model.Book;
import com.week10.api.model.BooksResponse;
import com.week10.api.spec.RequestSpecFactory;
import com.week10.api.spec.ResponseSpecFactory;
import com.week10.config.AppConfig;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * BooksClient – API Service Layer for the DemoQA BookStore.
 *
 * EVOLUTION from Week 9:
 *
 *   Week 9 BooksClient:
 *     - Built its own RequestSpecBuilder inline in the constructor
 *     - Had no shared response specifications
 *     - Allure filter registered once per client instance
 *
 *   Week 10 BooksClient:
 *     - Uses RequestSpecFactory.defaultSpec() → all clients share the same spec definition
 *     - Uses ResponseSpecFactory.ok200WithJson() → HTTP contract in one place
 *     - Exposes raw response AND typed methods (same as week9)
 *     - Adds getBookByIsbnRaw() for schema validation tests
 *     - Adds verifyBooksListSchema() for inline schema check
 *
 * SERVICE LAYER RULE:
 *   BooksClient is responsible for:
 *     ✓ Knowing the endpoint paths
 *     ✓ Returning typed Java objects
 *     ✓ Applying the shared spec
 *   BooksClient is NOT responsible for:
 *     ✗ TestNG assertions (those live in test classes)
 *     ✗ Selenide / browser interaction
 *     ✗ Schema validation logic (that lives in BookSchemaValidator)
 */
public class BooksClient {

    private static final Logger log = LoggerFactory.getLogger(BooksClient.class);

    private static final String BOOKS_PATH      = "/BookStore/v1/Books";
    private static final String BOOK_BY_ISBN    = "/BookStore/v1/Book";

    private final RequestSpecification spec;

    public BooksClient() {
        this.spec = RequestSpecFactory.defaultSpec();
        log.debug("BooksClient created with default spec (baseURI={})", AppConfig.getApiBaseUrl());
    }

    /** Package-private constructor for tests that need a custom spec (e.g. minimal/authenticated). */
    BooksClient(RequestSpecification customSpec) {
        this.spec = customSpec;
    }

    // =========================================================================
    // Raw Response methods
    // =========================================================================

    /**
     * Returns the raw Response for GET /BookStore/v1/Books.
     * Use when you need to assert status codes, headers, or run schema validation.
     */
    @Step("GET /BookStore/v1/Books – raw response")
    public Response getBooksRawResponse() {
        return RestAssured.given(spec)
                .get(BOOKS_PATH);
    }

    /**
     * Returns the raw Response for GET /BookStore/v1/Book?ISBN={isbn}.
     * DemoQA uses a query parameter for single-book lookup.
     */
    @Step("GET /BookStore/v1/Book?ISBN={isbn} – raw response")
    public Response getBookByIsbnRawResponse(String isbn) {
        return RestAssured.given(spec)
                .queryParam("ISBN", isbn)
                .get(BOOK_BY_ISBN);
    }

    // =========================================================================
    // Typed methods (ResponseSpec applied here)
    // =========================================================================

    /**
     * Returns the full list of books.
     * Validates HTTP 200 + JSON content type via ResponseSpecFactory.
     */
    @Step("Get all books")
    public List<Book> getAllBooks() {
        BooksResponse response = getBooksRawResponse()
                .then()
                .spec(ResponseSpecFactory.ok200WithJson())
                .extract()
                .as(BooksResponse.class);

        log.info("API returned {} books", response.getBooks().size());
        return response.getBooks();
    }

    /**
     * Returns a single book by ISBN using the /Book?ISBN= endpoint.
     * This endpoint returns a single Book object (not wrapped in a list).
     */
    @Step("Get book by ISBN: {isbn}")
    public Book getBookByIsbn(String isbn) {
        Book book = getBookByIsbnRawResponse(isbn)
                .then()
                .spec(ResponseSpecFactory.ok200WithJson())
                .extract()
                .as(Book.class);

        log.info("Retrieved book: {}", book);
        return book;
    }

    /**
     * Convenience – returns the first book in the catalogue.
     */
    @Step("Get first available book")
    public Book getFirstBook() {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) throw new IllegalStateException("No books in catalogue");
        return books.get(0);
    }

    // =========================================================================
    // Factory methods for special-spec clients
    // =========================================================================

    /** Creates a BooksClient backed by the minimal spec (no filters, no Allure). */
    public static BooksClient withMinimalSpec() {
        return new BooksClient(RequestSpecFactory.minimalSpec());
    }
}
