package com.week09.ui.pages;

import com.week09.ui.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * BookDetailPage – Page Object for the individual book detail view.
 *
 * URL: https://demoqa.com/books?book=<isbn>
 * Reached by clicking a book title on BookStorePage.
 *
 * Demonstrates:
 *   - isPageOpened() guard
 *   - Row-based data extraction (label → value)
 *   - Asserting field values obtained from the API against the UI display
 */
public class BookDetailPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    private final By bookTitle  = By.id("title-wrapper");
    private final By backButton = By.id("addNewRecordButton");

    /**
     * Returns a By locator for the value cell next to a given label.
     * The detail page uses a two-column layout: Label | Value
     *
     * XPath: find the row whose label cell matches, then get following-sibling value cell.
     * Example:
     *   label = "Author :"  →  following-sibling <span> contains the author name
     */
    private By valueByLabel(String labelText) {
        return By.xpath(
                "//div[@class='row']" +
                "[.//label[normalize-space()='" + labelText + "']]" +
                "//div[contains(@class,'col-')]"
        );
    }

    // =========================================================================
    // Guard
    // =========================================================================

    @Step("Verify Book Detail page is loaded")
    public BookDetailPage isPageOpened() {
        $(bookTitle).shouldBe(visible);
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    @Step("Go back to Book Store")
    public BookStorePage goBack() {
        $(backButton).click();
        return new BookStorePage();
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public String getTitleText() {
        return getText(bookTitle);
    }

    public String getAuthor() {
        return getText(valueByLabel("Author :"));
    }

    public String getPublisher() {
        return getText(valueByLabel("Publisher :"));
    }

    public String getIsbn() {
        return getText(valueByLabel("ISBN :"));
    }

    // =========================================================================
    // Assertions
    // =========================================================================

    @Step("Assert book title contains: {expected}")
    public BookDetailPage assertTitleContains(String expected) {
        $(bookTitle).shouldHave(text(expected));
        return this;
    }

    @Step("Assert author is: {expected}")
    public BookDetailPage assertAuthor(String expected) {
        $(valueByLabel("Author :")).shouldHave(text(expected));
        return this;
    }

    @Step("Assert ISBN is: {expected}")
    public BookDetailPage assertIsbn(String expected) {
        $(valueByLabel("ISBN :")).shouldHave(text(expected));
        return this;
    }
}
