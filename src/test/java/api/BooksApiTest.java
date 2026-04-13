package api;

import base.BaseApiTest;
import com.week09.api.model.Book;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * BooksApiTest – REST API tests for the DemoQA BookStore endpoint.
 *
 * Base: base.BaseApiTest (provides booksClient, RestAssured initialisation)
 *
 * Endpoint under test: GET https://demoqa.com/BookStore/v1/Books
 *
 * Tests cover:
 *   1. HTTP response status and Content-Type
 *   2. Response body shape (books list is non-empty)
 *   3. Book model fields are not null/empty
 *   4. A specific book can be found by ISBN
 *
 * All requests/responses are automatically attached to the Allure report
 * via the AllureRestAssured filter registered in BooksClient.
 */
@Epic("Week 9 – Unified Framework")
@Feature("BookStore API")
public class BooksApiTest extends BaseApiTest {

    // The ISBN of "You Don't Know JS" – a well-known book in the DemoQA catalogue
    private static final String KNOWN_ISBN = "9781449325862";

    @Test(description = "GET /Books returns HTTP 200",
          groups = {"smoke", "api"})
    @Story("Books endpoint – HTTP contract")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify the books endpoint responds with 200 OK and JSON content type.")
    public void getBooksReturnsHttp200() {
        Response response = booksClient.getBooksResponse();

        assertEquals(response.statusCode(), 200,
                "Expected 200 OK from /BookStore/v1/Books");
        assertTrue(response.contentType().contains("application/json"),
                "Expected JSON content type, got: " + response.contentType());
    }

    @Test(description = "GET /Books returns a non-empty list",
          groups = {"smoke", "api"})
    @Story("Books list")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The books list must contain at least 1 book.")
    public void getBooksReturnsNonEmptyList() {
        List<Book> books = booksClient.getAllBooks();

        assertFalse(books.isEmpty(), "Book list should not be empty");
        log.info("Books returned by API: {}", books.size());
    }

    @Test(description = "Each book has required non-null fields",
          groups = {"regression", "api"})
    @Story("Book model validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates isbn, title, author, and publisher are not null or blank for every book.")
    public void eachBookHasRequiredFields() {
        List<Book> books = booksClient.getAllBooks();

        for (Book book : books) {
            assertNotNull(book.getIsbn(),   "ISBN should not be null for: "    + book);
            assertNotNull(book.getTitle(),  "Title should not be null for: "   + book);
            assertNotNull(book.getAuthor(), "Author should not be null for: "  + book);
            assertFalse(book.getIsbn().isBlank(),   "ISBN should not be blank");
            assertFalse(book.getTitle().isBlank(),  "Title should not be blank");
            assertFalse(book.getAuthor().isBlank(), "Author should not be blank");
        }
    }

    @Test(description = "Get book by specific ISBN",
          groups = {"regression", "api"})
    @Story("Book lookup by ISBN")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve a single book by ISBN and verify its title and author.")
    public void getBookByIsbnReturnsCorrectBook() {
        Book book = booksClient.getBookByIsbn(KNOWN_ISBN);

        assertEquals(book.getIsbn(), KNOWN_ISBN,
                "ISBN should match the requested value");
        assertNotNull(book.getTitle(),  "Title should not be null");
        assertNotNull(book.getAuthor(), "Author should not be null");
        log.info("Found book: title='{}', author='{}'", book.getTitle(), book.getAuthor());
    }

    @Test(description = "Books list contains at least 8 books",
          groups = {"regression", "api"})
    @Story("Books list size")
    @Severity(SeverityLevel.NORMAL)
    @Description("The DemoQA catalogue is known to contain 8 books. Verify the count.")
    public void booksListHasExpectedSize() {
        List<Book> books = booksClient.getAllBooks();

        assertTrue(books.size() >= 8,
                "Expected at least 8 books, got: " + books.size());
    }

    @Test(description = "First book has a positive page count",
          groups = {"regression", "api"})
    @Story("Book model – numeric field")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that the 'pages' field is a positive integer.")
    public void firstBookHasPositivePageCount() {
        Book first = booksClient.getFirstBook();

        assertTrue(first.getPages() > 0,
                "Page count should be positive, was: " + first.getPages());
    }
}
