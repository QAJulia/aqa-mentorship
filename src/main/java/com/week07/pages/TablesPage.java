package com.week07.pages;

import com.week07.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TablesPage – Page Object for https://the-internet.herokuapp.com/tables
 *
 * Demonstrates the most advanced XPath patterns of Week 6:
 *  - Selecting a row by a specific cell value
 *  - following-sibling axis: get adjacent column values
 *  - ancestor axis: navigate from cell up to row, then down again
 *  - normalize-space() for whitespace-safe matching
 *  - Dynamic XPath construction (parameterised locators)
 */
public class TablesPage extends BasePage {

    // Table 1 id
    private static final String TABLE1 = "table1";
    private static final String TABLE2 = "table2";

    // =========================================================================
    // Static locators
    // =========================================================================

    // All header cells in Table 1
    private final By table1Headers =
            By.xpath("//table[@id='" + TABLE1 + "']//thead/tr/th");

    // All body rows in Table 1
    private final By table1Rows =
            By.xpath("//table[@id='" + TABLE1 + "']//tbody/tr");

    // Advanced XPath: the <a> delete link inside any row
    // following-sibling goes right from a <td> that holds the name
    private final By allDeleteLinks =
            By.xpath("//table[@id='" + TABLE1 + "']//td/a[text()='delete']");

    // Advanced XPath: all edit links
    private final By allEditLinks =
            By.xpath("//table[@id='" + TABLE1 + "']//td/a[text()='edit']");

    // =========================================================================
    // Constructor
    // =========================================================================

    public TablesPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public TablesPage open(String baseUrl) {
        driver.get(baseUrl + "/tables");
        return this;
    }

    // =========================================================================
    // Dynamic (parameterised) locators
    // =========================================================================

    /**
     * Returns the XPath locator for the row in table1 where the Last Name column
     * exactly matches the provided value.
     *
     * XPath breakdown:
     *   //table[@id='table1']  → table with id table1
     *   //tbody/tr             → any body row
     *   [td[1][normalize-space()='Smith']]  → where first <td> equals 'Smith'
     */
    private By rowByLastName(String lastName) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//tbody/tr" +
                "[td[1][normalize-space()='" + lastName + "']]"
        );
    }

    /**
     * Returns the locator for the email cell of a row identified by last name.
     *
     * Uses following-sibling to jump from the last-name cell to the email cell
     * (which is 3 positions to the right: [1]=first name, [2]=email, [3]=due, [4]=web, [5]=action)
     *
     * Axis: td[normalize-space()='lastName'] / following-sibling::td[2]
     */
    private By emailCellByLastName(String lastName) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//tbody/tr/" +
                "td[normalize-space()='" + lastName + "']/following-sibling::td[2]"
        );
    }

    /**
     * Returns the locator for the action link (edit/delete) for a given last name.
     *
     * Uses ancestor axis: start at the matching <td>, go UP to the <tr>,
     * then DOWN to the last <td>'s link.
     */
    private By actionLinkByLastName(String lastName, String action) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//td[normalize-space()='" + lastName + "']" +
                "/ancestor::tr/td[last()]/a[text()='" + action + "']"
        );
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public TablesPage clickEditFor(String lastName) {
        clickWhenReady(actionLinkByLastName(lastName, "edit"));
        return this;
    }

    public TablesPage clickDeleteFor(String lastName) {
        clickWhenReady(actionLinkByLastName(lastName, "delete"));
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    /**
     * Returns the text of all header cells in Table 1.
     */
    public List<String> getTable1Headers() {
        return driver.findElements(table1Headers)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of body rows in Table 1.
     */
    public int getTable1RowCount() {
        return driver.findElements(table1Rows).size();
    }

    /**
     * Returns whether a row with the given last name exists in Table 1.
     */
    public boolean hasRowWithLastName(String lastName) {
        return !driver.findElements(rowByLastName(lastName)).isEmpty();
    }

    /**
     * Returns the email cell value for a row identified by last name.
     *
     * Uses the following-sibling XPath pattern.
     */
    public String getEmailByLastName(String lastName) {
        return getText(emailCellByLastName(lastName));
    }

    /**
     * Returns the full text of a row as a list of cell strings.
     * Useful for asserting all columns at once.
     */
    public List<String> getRowCellsByLastName(String lastName) {
        WebElement row = driver.findElement(rowByLastName(lastName));
        return row.findElements(By.tagName("td"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of "delete" action links visible in Table 1.
     */
    public int getDeleteLinkCount() {
        return driver.findElements(allDeleteLinks).size();
    }

    /**
     * Returns the number of "edit" action links visible in Table 1.
     */
    public int getEditLinkCount() {
        return driver.findElements(allEditLinks).size();
    }
}
