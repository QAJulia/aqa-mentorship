package com.week06.base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage – superclass for all Page Object classes.
 *
 * Contains:
 *  - A reference to the shared WebDriver instance
 *  - A pre-configured WebDriverWait (explicit wait)
 *  - Common helper methods used across multiple pages
 *
 * Every concrete page class extends BasePage and calls super(driver).
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    /**
     * Constructor – all page objects receive the driver from the test.
     *
     * @param driver the active WebDriver session
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }

    // =========================================================================
    // Core interaction helpers
    // =========================================================================

    /**
     * Wait until the element is visible, then return it.
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait until the element is clickable, then click it.
     */
    protected void clickWhenReady(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Wait until the element is visible, clear it, then type.
     */
    protected void typeInto(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Return the trimmed text of the first matching element.
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Return all matching elements (no wait – use when list may be empty).
     */
    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Check whether an element is currently displayed (non-throwing).
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check whether a checkbox/radio is selected.
     */
    protected boolean isSelected(By locator) {
        return driver.findElement(locator).isSelected();
    }

    /**
     * Select an <option> by visible text inside a <select> element.
     */
    protected void selectByVisibleText(By locator, String text) {
        new Select(waitForVisible(locator)).selectByVisibleText(text);
    }

    /**
     * Return the currently selected option text from a <select> element.
     */
    protected String getSelectedOption(By locator) {
        return new Select(waitForVisible(locator)).getFirstSelectedOption().getText();
    }

    // =========================================================================
    // Page-level utilities
    // =========================================================================

    /**
     * Return the current page title.
     */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Return the current URL.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
