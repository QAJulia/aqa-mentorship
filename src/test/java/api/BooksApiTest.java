package api;

import base.BaseApiTest;
import com.week10.api.model.Book;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * BooksApiTest – comprehensive API tests for GET /BookStore/v1/Books.
 *
 * ALLURE TAGS STRATEGY (Week 10 deliverable):
 *
 *   @Epic    → top-level business domain ("Week 10 – Advanced REST Assured")
 *   @Feature → the specific API feature ("Books Catalogue API")
 *   @Story   → individual user story / behaviour being verified
 *   @Severity → BLOCKER > CRITICAL > NORMAL > MINOR > TRIVIAL
 *   @TmsLink  → link to a test management system ticket (e.g. Jira)
 *   @Issue    → link to a bug tracker issue
 *   @Link     → any external resource (API docs, Swagger, etc.)
 *   @Owner    → name of the test owner
 *
 * WHY CATEGORISE?
 *   In Allure you can group tests by Epic, Feature, Story in the "Behaviours" tab.
 *   This makes the report readable for non-technical stakeholders:
 *     "Books Catalogue API > Successful GET > Returns HTTP 200" — clear without code.
 *
 *   The "Categories" tab additionally groups failures by type
 *   (product defect, test defect, infrastructure problem).
 */
@Epic("Week 10 – Advanced REST Assured")
@Feature("Books Catalogue API")
@Owner("QA Mentorship")
@Link(name = "API Docs", url = "https://demoqa.com/swagger/")
public class BooksApiTest extends BaseApiTest {

    private static final String KNOWN_ISBN = "9781449325862";

    // =========================================================================
    // Smoke – HTTP contract
    // =========================================================================

    @Test(description = "GET /Books returns HTTP 200 with JSON",
          groups = {"smoke", "api", "contract"})
    @Story("Successful GET – HTTP contract")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies the endpoint responds with 200 OK and application/json content type.")
    public void getBooksReturns200Json() {
        Response response = booksClient.getBooksRawResponse();

        assertEquals(response.statusCode(), 200,
                "Expected HTTP 200 from /BookStore/v1/Books");
        assertTrue(response.contentType().contains("application/json"),
                "Expected JSON content type, got: " + response.contentType());
    }

    @Test(description = "GET /Books responds within 3 seconds",
          groups = {"smoke", "api", "performance"})
    @Story("Response time")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Response time check: expects the endpoint to respond under 3000 ms.")
    public void getBooksRespondsWithin3Seconds() {
        Response response = booksClient.getBooksRawResponse();

        long time = response.time();
        assertTrue(time < 3000,
                "Expected response in < 3000 ms, got: " + time + " ms");
        log.info("Response time: {} ms", time);
    }

    // =========================================================================
    // Regression – business rules
    // =========================================================================

    @Test(description = "Books list is non-empty (minimum 8 books)",
          groups = {"regression", "api", "books-list"})
    @Story("Books list – size")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The DemoQA catalogue always has at least 8 books.")
    public void booksListIsNonEmpty() {
        List<Book> books = booksClient.getAllBooks();

        assertFalse(books.isEmpty(), "Book list must not be empty");
        assertTrue(books.size() >= 8,
                "Expected at least 8 books, got: " + books.size());
        log.info("Catalogue size: {}", books.size());
    }

    @Test(description = "Each book has required non-null, non-blank fields",
          groups = {"regression", "api", "model"})
    @Story("Book model – required fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates isbn, title, author are non-null and non-blank for every book.")
    public void eachBookHasRequiredFields() {
        List<Book> books = booksClient.getAllBooks();

        for (Book book : books) {
            String context = "Book: " + book;
            assertNotNull(book.getIsbn(),   "isbn null for " + context);
            assertNotNull(book.getTitle(),  "title null for " + context);
            assertNotNull(book.getAuthor(), "author null for " + context);
            assertFalse(book.getIsbn().isBlank(),   "isbn blank for " + context);
            assertFalse(book.getTitle().isBlank(),  "title blank for " + context);
            assertFalse(book.getAuthor().isBlank(), "author blank for " + context);
        }
    }

    @Test(description = "Each book has a positive page count",
          groups = {"regression", "api", "model"})
    @Story("Book model – numeric fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Page count must be >= 1 for every book.")
    public void eachBookHasPositivePageCount() {
        booksClient.getAllBooks().forEach(book ->
                assertTrue(book.getPages() >= 1,
                        "Expected pages >= 1 for: " + book)
        );
    }

    @Test(description = "Get book by specific ISBN returns correct record",
          groups = {"regression", "api", "single-book"})
    @Story("Single book lookup – by ISBN")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("TC-101")
    @Description("GET /BookStore/v1/Book?ISBN=9781449325862 returns the correct book record.")
    public void getBookByIsbnReturnsCorrectRecord() {
        Book book = booksClient.getBookByIsbn(KNOWN_ISBN);

        assertEquals(book.getIsbn(), KNOWN_ISBN, "ISBN mismatch");
        assertNotNull(book.getTitle(),  "Title must not be null");
        assertNotNull(book.getAuthor(), "Author must not be null");
        log.info("Verified book by ISBN: {}", book);
    }

    @Test(description = "Each book's website field is a non-blank URI",
          groups = {"regression", "api", "model"})
    @Story("Book model – website URI field")
    @Severity(SeverityLevel.MINOR)
    @Description("The 'website' field should be a non-blank string (URI format validated by schema).")
    public void eachBookHasNonBlankWebsite() {
        booksClient.getAllBooks().forEach(book ->
                assertFalse(book.getWebsite() == null || book.getWebsite().isBlank(),
                        "Website is null/blank for: " + book)
        );
    }
}
