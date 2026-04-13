package ui;

import base.BaseUiTest;
import com.week09.ui.pages.BookStorePage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * BookStoreUiTest – Selenide UI tests for the DemoQA Book Store page.
 *
 * Base: base.BaseUiTest (provides Selenide config, closeWebDriver on teardown)
 *
 * Page under test: https://demoqa.com/books
 *
 * Tests cover:
 *   1. Page loads and shows books
 *   2. Search/filter functionality
 *   3. Book title links are visible
 */
@Epic("Week 9 – Unified Framework")
@Feature("BookStore UI")
public class BookStoreUiTest extends BaseUiTest {

    @Test(description = "BookStore page opens and shows books",
          groups = {"smoke", "ui"})
    @Story("Page loads")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Open https://demoqa.com/books and verify at least one book is displayed.")
    public void bookStorePageLoadsWithBooks() {
        BookStorePage page = new BookStorePage().open().isPageOpened();

        assertTrue(page.getBookCount() > 0,
                "At least one book should be visible on the page");
        log.info("UI shows {} books on load", page.getBookCount());
    }

    @Test(description = "All book titles are non-empty strings",
          groups = {"smoke", "ui"})
    @Story("Book title content")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify every title link on the page has non-empty text.")
    public void allBookTitlesAreNonEmpty() {
        List<String> titles = new BookStorePage().open().isPageOpened().getAllBookTitles();

        assertFalse(titles.isEmpty(), "Title list should not be empty");
        titles.forEach(title ->
                assertFalse(title.isBlank(), "Title should not be blank, was: [" + title + "]")
        );
        log.info("Verified {} book titles on UI", titles.size());
    }

    @Test(description = "Search filters the book list to matching results",
          groups = {"regression", "ui"})
    @Story("Search / filter")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Type a known title fragment in the search box and verify only matching books are shown.")
    public void searchFiltersBooks() {
        BookStorePage page = new BookStorePage().open().isPageOpened();

        // Use a partial title from a well-known book in the catalogue
        page.searchFor("JavaScript");

        List<String> filtered = page.getAllBookTitles();
        assertFalse(filtered.isEmpty(),
                "Search for 'JavaScript' should return at least one result");

        // Every remaining title must contain the search term (case-insensitive)
        filtered.forEach(title ->
                assertTrue(title.toLowerCase().contains("javascript"),
                        "Unexpected title in filtered results: " + title)
        );

        log.info("Search 'JavaScript' returned {} result(s)", filtered.size());
    }

    @Test(description = "A specific book title is visible on the page",
          groups = {"regression", "ui"})
    @Story("Book visibility by title")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert a well-known title from the catalogue is visible on the UI.")
    public void knownBookTitleIsVisible() {
        new BookStorePage()
                .open()
                .isPageOpened()
                .assertBookVisible("You Don't Know JS");
    }
}
