package com.week06.pages;

import com.week06.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DropdownPage – Page Object for https://the-internet.herokuapp.com/dropdown
 *
 * Demonstrates:
 *  - Working with <select> elements via Selenium's Select class (wrapped in BasePage)
 *  - XPath: //option[not(@disabled)] – selecting non-disabled options
 *  - XPath: //option[@selected] – getting the currently selected option
 *  - preceding-sibling axis to find the label that precedes the dropdown
 */
public class DropdownPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // The <select> element
    private final By dropdown = By.id("dropdown");

    // Advanced XPath: all <option> elements that are NOT disabled
    // Use case: the first option "Please select an option" has @disabled,
    //           this selector gives only valid choices
    private final By enabledOptions =
            By.xpath("//select[@id='dropdown']/option[not(@disabled)]");

    // Advanced XPath: the currently selected option
    private final By selectedOption =
            By.xpath("//select[@id='dropdown']/option[@selected]");

    // Advanced XPath: the <label> for the dropdown.
    // preceding-sibling navigates left among siblings to find elements
    // that share the same parent and come BEFORE the <select>
    private final By dropdownLabel =
            By.xpath("//select[@id='dropdown']/preceding-sibling::label");

    // Page heading
    private final By heading = By.xpath("//h3[contains(text(),'Dropdown')]");

    // =========================================================================
    // Constructor
    // =========================================================================

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public DropdownPage open(String baseUrl) {
        driver.get(baseUrl + "/dropdown");
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    /**
     * Selects an option by its visible text (e.g. "Option 1", "Option 2").
     */
    public DropdownPage selectOption(String visibleText) {
        selectByVisibleText(dropdown, visibleText);
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    /**
     * Returns the currently selected option's text.
     */
    public String getSelectedOptionText() {
        return getSelectedOption(dropdown);
    }

    /**
     * Returns a list of all selectable (non-disabled) option texts.
     */
    public List<String> getAvailableOptions() {
        return driver.findElements(enabledOptions)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of selectable options (excludes the disabled placeholder).
     */
    public int getEnabledOptionCount() {
        return driver.findElements(enabledOptions).size();
    }

    /**
     * Returns the label text displayed above the dropdown.
     * Uses the preceding-sibling axis.
     *
     * Note: the-internet does not have a <label> here, so this demonstrates
     * the concept; if absent the method returns an empty string gracefully.
     */
    public String getDropdownLabelText() {
        List<WebElement> labels = driver.findElements(dropdownLabel);
        return labels.isEmpty() ? "" : labels.get(0).getText().trim();
    }

    public String getHeading() {
        return getText(heading);
    }
}
