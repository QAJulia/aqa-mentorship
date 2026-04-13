package com.week09.ui.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.week09.ui.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * BookStorePage – Page Object for https://demoqa.com/books
 *
 * This page displays the same book catalogue that is also available via the
 * BookStore REST API. This makes it ideal for cross-layer (API ↔ UI) tests:
 *
 *   API: GET /BookStore/v1/Books → returns a list of books with titles
 *   UI:  demoqa.com/books        → displays those same titles in a table
 *
 * Demonstrates:
 *   - isPageOpened() guard
 *   - XPath: following-sibling and text-based selectors in a real table
 *   - Search / filter interaction
 *   - Cross-layer assertion: "every title returned by the API is visible on the UI"
 */
public class BookStorePage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // The search input at the top of the book list
    private final By searchInput  = By.id("searchBox");

    // All book title links in the table
    // XPath: <a> elements that are descendants of the table rows
    private final By bookTitleLinks =
            By.xpath("//div[@class='rt-tr-group']//a");

    // Total rows in the books table (including empty placeholder rows)
    private final By tableRows =
            By.xpath("//div[contains(@class,'rt-tr-group')]");

    // Rows that have actual content (non-empty first cell)
    private final By nonEmptyRows =
            By.xpath("//div[contains(@class,'rt-tr-group')][.//a]");

    // Page heading
    private final By heading = By.xpath("//div[@class='main-header']");

    // =========================================================================
    // Navigation + Guard
    // =========================================================================

    @Step("Open Book Store page")
    public BookStorePage open() {
        Selenide.open("/books");
        log.debug("Opened BookStore page");
        return this;
    }

    /**
     * isPageOpened() guard: waits for the search box and at least one book title.
     */
    @Step("Verify Book Store page is loaded")
    public BookStorePage isPageOpened() {
        $(searchInput).shouldBe(visible);
        $$(bookTitleLinks).shouldHave(CollectionCondition.sizeGreaterThan(0));
        log.debug("BookStorePage is open, {} books visible", countElements(bookTitleLinks));
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    @Step("Search for book: {searchTerm}")
    public BookStorePage searchFor(String searchTerm) {
        $(searchInput).val(searchTerm);
        log.debug("Searched for: {}", searchTerm);
        return this;
    }

    @Step("Clear search input")
    public BookStorePage clearSearch() {
        $(searchInput).val("");
        return this;
    }

    @Step("Click on book title: {title}")
    public BookDetailPage clickBook(String title) {
        By titleLink = By.xpath("//a[normalize-space()='" + title + "']");
        $(titleLink).shouldBe(visible).click();
        log.debug("Clicked book: {}", title);
        return new BookDetailPage();
    }

    // =========================================================================
    // Queries
    // =========================================================================

    /** Returns the text of all book title links currently visible. */
    @Step("Get all visible book titles")
    public List<String> getAllBookTitles() {
        return $$(bookTitleLinks).texts();
    }

    /** Returns the number of non-empty book rows. */
    public int getBookCount() {
        return $$(nonEmptyRows).size();
    }

    public String getHeadingText() {
        return getText(heading);
    }

    // =========================================================================
    // Assertions
    // =========================================================================

    /** Asserts that a book with the given title is visible on the current page. */
    @Step("Assert book title is visible: {expectedTitle}")
    public BookStorePage assertBookVisible(String expectedTitle) {
        By titleLink = By.xpath("//a[normalize-space()='" + expectedTitle + "']");
        $(titleLink).shouldBe(visible);
        log.debug("Asserted book is visible: {}", expectedTitle);
        return this;
    }

    /** Asserts that a book with the given title is NOT visible (e.g. filtered out). */
    @Step("Assert book title is absent: {title}")
    public BookStorePage assertBookAbsent(String title) {
        By titleLink = By.xpath("//a[normalize-space()='" + title + "']");
        $(titleLink).shouldNotBe(visible);
        return this;
    }

    /** Asserts the book count (non-empty rows) equals the expected value. */
    @Step("Assert book count: {expected}")
    public BookStorePage assertBookCount(int expected) {
        $$(nonEmptyRows).shouldHave(CollectionCondition.size(expected));
        return this;
    }
}
