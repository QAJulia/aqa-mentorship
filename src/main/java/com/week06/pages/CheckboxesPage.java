package com.week06.pages;

import com.week06.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * CheckboxesPage – Page Object for https://the-internet.herokuapp.com/checkboxes
 *
 * Demonstrates:
 *  - Positional XPath predicates: input[1], input[last()]
 *  - Parent axis: from an input back to its containing form
 *  - descendant axis: collecting all checkboxes under the form
 *  - Conditional state checks (isSelected)
 */
public class CheckboxesPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // The form wrapper that contains all checkboxes
    private final By checkboxForm = By.id("checkboxes");

    // Advanced XPath: positional predicate
    // [1] = first checkbox; [last()] = second (last) checkbox
    private final By firstCheckbox  = By.xpath("//form[@id='checkboxes']/input[1]");
    private final By lastCheckbox   = By.xpath("//form[@id='checkboxes']/input[last()]");

    // Advanced XPath: descendant axis – all inputs under the form
    private final By allCheckboxes  = By.xpath("//form[@id='checkboxes']/descendant::input");

    // Advanced XPath: filter only CHECKED checkboxes
    private final By checkedBoxes   = By.xpath("//form[@id='checkboxes']/input[@checked]");

    // Page heading
    private final By heading = By.xpath("//h3[normalize-space()='Checkboxes']");

    // =========================================================================
    // Constructor
    // =========================================================================

    public CheckboxesPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public CheckboxesPage open(String baseUrl) {
        driver.get(baseUrl + "/checkboxes");
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    /**
     * Checks the first checkbox only if it is not already checked.
     */
    public CheckboxesPage checkFirst() {
        if (!isFirstChecked()) {
            driver.findElement(firstCheckbox).click();
        }
        return this;
    }

    /**
     * Unchecks the last checkbox only if it is currently checked.
     */
    public CheckboxesPage uncheckLast() {
        if (isLastChecked()) {
            driver.findElement(lastCheckbox).click();
        }
        return this;
    }

    /**
     * Ensures all checkboxes are checked.
     */
    public CheckboxesPage checkAll() {
        getAllCheckboxes().stream()
                .filter(cb -> !cb.isSelected())
                .forEach(WebElement::click);
        return this;
    }

    /**
     * Ensures all checkboxes are unchecked.
     */
    public CheckboxesPage uncheckAll() {
        getAllCheckboxes().stream()
                .filter(WebElement::isSelected)
                .forEach(WebElement::click);
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public boolean isFirstChecked() {
        return driver.findElement(firstCheckbox).isSelected();
    }

    public boolean isLastChecked() {
        return driver.findElement(lastCheckbox).isSelected();
    }

    public int getTotalCheckboxCount() {
        return getAllCheckboxes().size();
    }

    public int getCheckedCount() {
        return driver.findElements(checkedBoxes).size();
    }

    public String getHeading() {
        return getText(heading);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private List<WebElement> getAllCheckboxes() {
        return findAll(allCheckboxes);
    }
}
