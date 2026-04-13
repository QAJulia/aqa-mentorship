package com.week07.pages.fluent;

import com.week07.base.BasePage;
import com.week07.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * FluentLoginPage – Fluent Page Object for the login page.
 *
 * Key differences from the Classic POM LoginPage (Week 6):
 *
 *  1. open() uses TestConfig instead of accepting a parameter –
 *     the page knows its own URL from the config layer.
 *
 *  2. isPageOpened() is a GUARD: it blocks until both the correct URL
 *     and a key element are visible. Call it immediately after open().
 *     Returns 'this' so it can be chained.
 *
 *  3. Every action returns 'this' (same page) or a new page object.
 *     Tests never hold intermediate variables; the chain tells the story.
 *
 *  4. Assert methods (assertXxx) live here for readability when the
 *     assertion logically belongs to the page context. They return 'this'
 *     so assertions can be chained with actions.
 */
public class FluentLoginPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    private final By usernameInput  = By.id("username");
    private final By passwordInput  = By.id("password");
    private final By loginButton    = By.xpath("//button[contains(@class,'radius')]");
    private final By flashMessage   = By.xpath("//div[starts-with(@id,'flash')]");
    private final By formHeading    = By.xpath("//input[@id='username']/ancestor::form/h4");

    // =========================================================================
    // Constructor
    // =========================================================================

    public FluentLoginPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation + Guard
    // =========================================================================

    /**
     * Navigate to the login page using URL from TestConfig.
     * Returns this for chaining: new FluentLoginPage(driver).open().isPageOpened()
     */
    public FluentLoginPage open() {
        driver.get(TestConfig.getLoginUrl());
        return this;
    }

    /**
     * isPageOpened() – Loadable Component guard.
     *
     * Asserts that:
     *  1. The current URL contains "/login"
     *  2. The login button is visible
     *
     * Throws TimeoutException (→ test failure) with a clear message if
     * either condition is not met within the configured timeout.
     *
     * Call this immediately after open() and before any interaction.
     */
    public FluentLoginPage isPageOpened() {
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        return this;
    }

    // =========================================================================
    // Actions – return 'this' (same page)
    // =========================================================================

    public FluentLoginPage enterUsername(String username) {
        typeInto(usernameInput, username);
        return this;
    }

    public FluentLoginPage enterPassword(String password) {
        typeInto(passwordInput, password);
        return this;
    }

    // =========================================================================
    // Navigation action – returns the NEXT page
    // =========================================================================

    /**
     * Clicks the login button and returns the SecureAreaFluentPage.
     * The driver will be on /secure after a successful login.
     */
    public FluentSecureAreaPage clickLogin() {
        clickWhenReady(loginButton);
        return new FluentSecureAreaPage(driver);
    }

    /**
     * Clicks login but stays on LoginPage (for failed login scenarios).
     * Use this variant when you expect the login to FAIL.
     */
    public FluentLoginPage clickLoginExpectingFailure() {
        clickWhenReady(loginButton);
        return this;
    }

    // =========================================================================
    // Assertion methods – return 'this' for chaining
    // =========================================================================

    /**
     * Asserts that the flash message is visible and contains the expected text.
     */
    public FluentLoginPage assertFlashContains(String expectedText) {
        String actual = getText(flashMessage);
        if (!actual.contains(expectedText)) {
            throw new AssertionError(
                    "Flash message expected to contain: [" + expectedText +
                    "] but was: [" + actual + "]"
            );
        }
        return this;
    }

    /**
     * Asserts the login form heading equals the expected value.
     */
    public FluentLoginPage assertHeadingEquals(String expected) {
        String actual = getText(formHeading);
        if (!actual.equals(expected)) {
            throw new AssertionError(
                    "Login heading expected: [" + expected + "] but was: [" + actual + "]"
            );
        }
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public String getFlashText() {
        return getText(flashMessage);
    }

    public boolean isFlashVisible() {
        return isDisplayed(flashMessage);
    }
}
