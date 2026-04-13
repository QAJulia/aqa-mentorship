package com.week09.api.client;

import com.week09.api.model.Book;
import com.week09.api.model.BooksResponse;
import com.week09.config.AppConfig;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * BooksClient – REST Assured based API client for the DemoQA BookStore.
 *
 * RESPONSIBILITIES:
 *   - Build and hold a shared RequestSpecification (base URL, headers, logging)
 *   - Provide typed methods that map API responses to Java models
 *   - Attach every request/response to the Allure report via AllureRestAssured filter
 *
 * ARCHITECTURE:
 *   Tests never call RestAssured.given() directly.
 *   They call BooksClient methods: getBooksResponse(), getAllBooks(), getBookByIsbn().
 *   This centralises all HTTP concerns in one place.
 *
 * USAGE in BaseApiTest:
 *   protected BooksClient booksClient = new BooksClient();
 *
 *   // In test:
 *   List<Book> books = booksClient.getAllBooks();
 */
public class BooksClient {

    private static final Logger log = LoggerFactory.getLogger(BooksClient.class);
    private final RequestSpecification spec;

    public BooksClient() {
        spec = new RequestSpecBuilder()
                .setBaseUri(AppConfig.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.METHOD)      // log HTTP method to console
                .addFilter(new AllureRestAssured()) // attach req/resp to Allure
                .build();

        log.debug("BooksClient initialised with base URI: {}", AppConfig.getApiBaseUrl());
    }

    // =========================================================================
    // Raw response – use when you need to assert status codes etc.
    // =========================================================================

    /**
     * Sends GET /BookStore/v1/Books and returns the raw Response.
     * Use this when you need to assert the HTTP status code or headers directly.
     */
    @Step("GET all books – raw response")
    public Response getBooksResponse() {
        log.debug("GET {}", AppConfig.getBookStorePath());
        return RestAssured.given(spec)
                .get(AppConfig.getBookStorePath());
    }

    // =========================================================================
    // Typed methods – use in most tests
    // =========================================================================

    /**
     * Returns all books as a typed Java list.
     * Jackson automatically maps the JSON response to BooksResponse → List<Book>.
     */
    @Step("Get all books")
    public List<Book> getAllBooks() {
        BooksResponse response = getBooksResponse()
                .then()
                .statusCode(200)
                .extract()
                .as(BooksResponse.class);

        List<Book> books = response.getBooks();
        log.debug("Retrieved {} books from API", books.size());
        return books;
    }

    /**
     * Returns a single book by ISBN from the books list.
     * Throws IllegalArgumentException if not found.
     */
    @Step("Get book by ISBN: {isbn}")
    public Book getBookByIsbn(String isbn) {
        return getAllBooks().stream()
                .filter(b -> isbn.equals(b.getIsbn()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book with ISBN [" + isbn + "] not found"
                ));
    }

    /**
     * Returns the first book in the list.
     * Convenience method for tests that just need any book.
     */
    @Step("Get first available book")
    public Book getFirstBook() {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) {
            throw new IllegalStateException("No books available in the BookStore");
        }
        return books.get(0);
    }
}
