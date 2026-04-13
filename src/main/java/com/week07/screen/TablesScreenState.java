package com.week07.screen;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TablesScreenState – Screen/State Object for the tables page.
 *
 * PURPOSE:
 * A Screen object reads the current visible state of the UI and provides
 * fluent ASSERTION methods. It does NOT perform user actions (no clicking,
 * no typing). That separation makes it clear whether a method changes
 * the page or merely reads it.
 *
 * CONTRAST with TablesPage (page object):
 *   TablesPage    → "DO things": click sort, click edit, navigate
 *   TablesScreenState → "VERIFY things": assert row exists, assert headers, etc.
 *
 * USAGE in tests:
 *   new TablesScreenState(driver)
 *       .assertRowCount(4)
 *       .assertRowExists("Smith")
 *       .assertColumnHeaders("Last Name", "First Name", "Email", "Due", "Web Site", "Action")
 *       .assertEmailForRow("Smith", "jsmith@gmail.com");
 */
public class TablesScreenState {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String TABLE_ID = "table1";

    // Locators (read-only: no click/sendKeys ever called on these)
    private final By headers    = By.xpath("//table[@id='" + TABLE_ID + "']//thead/tr/th");
    private final By bodyRows   = By.xpath("//table[@id='" + TABLE_ID + "']//tbody/tr");

    public TablesScreenState(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // =========================================================================
    // Fluent assertion methods – every method returns 'this'
    // =========================================================================

    /**
     * Asserts the total number of data rows equals the expected count.
     */
    public TablesScreenState assertRowCount(int expected) {
        int actual = driver.findElements(bodyRows).size();
        if (actual != expected) {
            throw new AssertionError(
                    "Table row count: expected [" + expected + "] but was [" + actual + "]"
            );
        }
        return this;
    }

    /**
     * Asserts that a row with the given last name exists.
     * Uses normalize-space() to ignore leading/trailing whitespace.
     */
    public TablesScreenState assertRowExists(String lastName) {
        String xpath = "//table[@id='" + TABLE_ID + "']//tbody/tr" +
                       "[td[1][normalize-space()='" + lastName + "']]";
        List<WebElement> matches = driver.findElements(By.xpath(xpath));
        if (matches.isEmpty()) {
            throw new AssertionError(
                    "Expected a row with last name [" + lastName + "] but none was found"
            );
        }
        return this;
    }

    /**
     * Asserts that NO row with the given last name exists.
     */
    public TablesScreenState assertRowAbsent(String lastName) {
        String xpath = "//table[@id='" + TABLE_ID + "']//tbody/tr" +
                       "[td[1][normalize-space()='" + lastName + "']]";
        List<WebElement> matches = driver.findElements(By.xpath(xpath));
        if (!matches.isEmpty()) {
            throw new AssertionError(
                    "Expected no row with last name [" + lastName + "] but one was found"
            );
        }
        return this;
    }

    /**
     * Asserts the column headers exactly match the provided values in order.
     */
    public TablesScreenState assertColumnHeaders(String... expectedHeaders) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(headers));
        List<String> actual = driver.findElements(headers).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        List<String> expected = Arrays.asList(expectedHeaders);
        if (!actual.equals(expected)) {
            throw new AssertionError(
                    "Column headers mismatch.\n  Expected: " + expected +
                    "\n  Actual:   " + actual
            );
        }
        return this;
    }

    /**
     * Asserts the email column value for the row identified by last name.
     * Uses the following-sibling XPath axis.
     */
    public TablesScreenState assertEmailForRow(String lastName, String expectedEmail) {
        String xpath = "//table[@id='" + TABLE_ID + "']//tbody/tr/" +
                       "td[normalize-space()='" + lastName + "']/following-sibling::td[2]";
        String actual = driver.findElement(By.xpath(xpath)).getText().trim();
        if (!actual.equals(expectedEmail)) {
            throw new AssertionError(
                    "Email for [" + lastName + "]: expected [" + expectedEmail +
                    "] but was [" + actual + "]"
            );
        }
        return this;
    }

    /**
     * Asserts that every row has both an edit and a delete action link.
     */
    public TablesScreenState assertAllRowsHaveActionLinks() {
        int rows        = driver.findElements(bodyRows).size();
        int editLinks   = driver.findElements(
                By.xpath("//table[@id='" + TABLE_ID + "']//td/a[text()='edit']")).size();
        int deleteLinks = driver.findElements(
                By.xpath("//table[@id='" + TABLE_ID + "']//td/a[text()='delete']")).size();

        if (editLinks != rows) {
            throw new AssertionError(
                    "Edit link count [" + editLinks + "] does not match row count [" + rows + "]"
            );
        }
        if (deleteLinks != rows) {
            throw new AssertionError(
                    "Delete link count [" + deleteLinks + "] does not match row count [" + rows + "]"
            );
        }
        return this;
    }

    // =========================================================================
    // State readers (non-asserting) – return raw values for use in tests
    // =========================================================================

    public int getRowCount() {
        return driver.findElements(bodyRows).size();
    }

    public List<String> getHeaders() {
        return driver.findElements(headers).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
