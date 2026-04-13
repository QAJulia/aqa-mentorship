package base;

import com.week09.api.client.BooksClient;
import com.week09.utils.RestAssuredConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * base.BaseApiTest – base class for all API tests.
 *
 * Extends base.BaseTest (root) and adds:
 *   1. REST Assured global initialisation in @BeforeSuite
 *   2. A fresh BooksClient instance before each test method
 *
 * DESIGN DECISIONS:
 *
 *   Q: Why create a new BooksClient per test (@BeforeMethod) instead of once per class?
 *   A: Each test should be independent. A new BooksClient starts with a clean
 *      RequestSpecification. For stateless GET tests it makes no functional difference,
 *      but it establishes the right habit for tests that involve auth tokens or state.
 *
 *   Q: Why not put BooksClient as a static field?
 *   A: Statics are shared across parallel tests and can cause race conditions.
 *      Instance fields per test are safe for parallel execution.
 *
 * API test classes extend base.BaseApiTest and use the 'booksClient' field directly.
 *
 * Example:
 *   public class BooksApiTest extends base.BaseApiTest {
 *       @Test
 *       public void getAllBooksReturnsNonEmptyList() {
 *           List<Book> books = booksClient.getAllBooks();
 *           assertFalse(books.isEmpty());
 *       }
 *   }
 */
public abstract class BaseApiTest extends BaseTest {

    protected BooksClient booksClient;

    @BeforeSuite(alwaysRun = true, dependsOnMethods = "logEnvironment")
    public void initApi() {
        RestAssuredConfig.init();
        log.info("REST Assured ready");
    }

    @BeforeMethod(alwaysRun = true)
    public void createApiClient() {
        booksClient = new BooksClient();
    }
}
