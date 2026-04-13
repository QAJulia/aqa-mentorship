package com.week07.pages.fluent;

import com.week07.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * FluentSecureAreaPage – the page shown after a successful login.
 *
 * Returned by FluentLoginPage.clickLogin().
 * Demonstrates:
 *  - isPageOpened() guard with URL + element check
 *  - Assertion methods that return 'this' for full chain continuation
 *  - Navigation back to login via clickLogout()
 */
public class FluentSecureAreaPage extends BasePage {

    private final By flashMessage = By.xpath("//div[contains(@class,'flash')]");
    private final By heading      = By.xpath("//h2");
    private final By logoutLink   = By.xpath("//a[contains(text(),'Logout')]");

    public FluentSecureAreaPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Guard
    // =========================================================================

    /**
     * Waits until the secure area URL and heading are visible.
     * Call immediately after clickLogin() in successful login scenarios.
     */
    public FluentSecureAreaPage isPageOpened() {
        wait.until(ExpectedConditions.urlContains("/secure"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public FluentLoginPage clickLogout() {
        clickWhenReady(logoutLink);
        return new FluentLoginPage(driver);
    }

    // =========================================================================
    // Assertions – return 'this' for chaining
    // =========================================================================

    public FluentSecureAreaPage assertWelcomeVisible() {
        if (!isDisplayed(flashMessage)) {
            throw new AssertionError("Welcome flash message is not visible on secure area page");
        }
        return this;
    }

    public FluentSecureAreaPage assertFlashContains(String text) {
        String actual = getText(flashMessage);
        if (!actual.contains(text)) {
            throw new AssertionError(
                    "Secure area flash expected to contain: [" + text + "] but was: [" + actual + "]"
            );
        }
        return this;
    }

    public FluentSecureAreaPage assertHeadingContains(String text) {
        String actual = getText(heading);
        if (!actual.contains(text)) {
            throw new AssertionError(
                    "Heading expected to contain: [" + text + "] but was: [" + actual + "]"
            );
        }
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public String getFlashText()   { return getText(flashMessage); }
    public String getHeadingText() { return getText(heading); }
}
