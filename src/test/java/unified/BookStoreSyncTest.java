package unified;

import base.BaseUiTest;
import com.week09.api.client.BooksClient;
import com.week09.api.model.Book;
import com.week09.ui.pages.BookStorePage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * BookStoreSyncTest – UNIFIED API + UI test class.
 *
 * This is the most important class in Week 9: it demonstrates why having
 * a coherent API + UI framework is valuable.
 *
 * PATTERN: "API-first verification"
 *   1. Fetch the ground truth from the API  →  fast, precise, authoritative
 *   2. Open the UI and verify the same data →  confirms the UI displays what the API returns
 *
 * This pattern is more reliable than verifying UI data alone because:
 *   - The API response is the source of truth
 *   - If the API changes, the test catches it at the API level (no need to open a browser)
 *   - The UI assertion confirms the rendering layer is consistent with the backend
 *
 * BASE CLASS:
 *   Extends base.BaseUiTest (provides Selenide + closeWebDriver teardown).
 *   Holds a BooksClient field directly instead of extending base.BaseApiTest
 *   (which would add unnecessary REST Assured @BeforeSuite to a UI test class).
 *   This is intentional — only take what you need from each layer.
 *
 * ALLURE REPORT:
 *   The test will show:
 *     - API step: request + response body (via AllureRestAssured filter)
 *     - UI steps: Selenide actions + screenshot on failure (via AllureSelenide)
 */
@Epic("Week 9 – Unified Framework")
@Feature("API ↔ UI Synchronisation")
public class BookStoreSyncTest extends BaseUiTest {

    private BooksClient booksClient;

    @BeforeMethod(alwaysRun = true)
    public void createClient() {
        // Initialise REST Assured config (idempotent)
        com.week09.utils.RestAssuredConfig.init();
        booksClient = new BooksClient();
    }

    // =========================================================================
    // Test 1 – Every API book title appears in the UI
    // =========================================================================

    @Test(description = "All book titles from the API are visible on the UI",
          groups = {"smoke", "unified"})
    @Story("API ↔ UI title sync")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "1. Fetch all books via API.\n" +
        "2. Open the BookStore UI page.\n" +
        "3. Assert every API title is present in the UI title list.\n" +
        "This test fails if the API returns a book that the UI does not display."
    )
    public void allApiBookTitlesAreVisibleOnUi() {
        // ── API step ─────────────────────────────────────────────────────────
        List<Book> apiBooks = booksClient.getAllBooks();
        List<String> apiTitles = apiBooks.stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());

        log.info("API returned {} books", apiTitles.size());

        // ── UI step ──────────────────────────────────────────────────────────
        BookStorePage uiPage = new BookStorePage().open().isPageOpened();
        List<String> uiTitles = uiPage.getAllBookTitles();

        log.info("UI shows {} book titles", uiTitles.size());

        // ── Assertion ─────────────────────────────────────────────────────────
        for (String apiTitle : apiTitles) {
            assertTrue(uiTitles.contains(apiTitle),
                    "API title not found on UI: [" + apiTitle + "]");
        }
    }

    // =========================================================================
    // Test 2 – Book count matches between API and UI
    // =========================================================================

    @Test(description = "Book count in the UI matches the API book count",
          groups = {"regression", "unified"})
    @Story("API ↔ UI count sync")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Compares the number of books returned by the API with the number " +
        "of books displayed on the UI page."
    )
    public void bookCountMatchesBetweenApiAndUi() {
        int apiCount = booksClient.getAllBooks().size();
        int uiCount  = new BookStorePage().open().isPageOpened().getBookCount();

        log.info("API count={}, UI count={}", apiCount, uiCount);

        assertEquals(uiCount, apiCount,
                "UI book count should match API book count");
    }

    // =========================================================================
    // Test 3 – Click a book on UI and verify detail matches the API record
    // =========================================================================

    @Test(description = "Book detail page shows correct data matching API record",
          groups = {"regression", "unified"})
    @Story("Book detail – API ↔ UI field match")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "1. Fetch the first book from the API.\n" +
        "2. Click that book on the UI.\n" +
        "3. Assert the detail page title and author match the API data."
    )
    public void bookDetailMatchesApiRecord() {
        // ── API: get the first book as source of truth ────────────────────
        Book apiBook = booksClient.getFirstBook();
        log.info("API first book: {}", apiBook);

        // ── UI: open store, click the book, verify detail ─────────────────
        new BookStorePage()
                .open()
                .isPageOpened()
                .clickBook(apiBook.getTitle())
                .isPageOpened()
                .assertTitleContains(apiBook.getTitle())
                .assertAuthor(apiBook.getAuthor())
                .assertIsbn(apiBook.getIsbn());
    }
}
