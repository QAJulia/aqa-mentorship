package com.week07.screen;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginScreenState – Screen/State Object for the login page.
 *
 * Reads and asserts the visual state of the login page.
 * Never performs actions (no click, no sendKeys).
 *
 * Typical use in tests:
 *
 *   new LoginScreenState(driver)
 *       .assertOnLoginPage()
 *       .assertFlashContains("Your password is invalid")
 *       .assertFormVisible();
 */
public class LoginScreenState {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By flashMessage  = By.xpath("//div[starts-with(@id,'flash')]");
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton   = By.xpath("//button[contains(@class,'radius')]");
    private final By formHeading   = By.xpath("//input[@id='username']/ancestor::form/h4");

    public LoginScreenState(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // =========================================================================
    // Fluent assertion methods
    // =========================================================================

    /** Asserts the current URL ends with /login. */
    public LoginScreenState assertOnLoginPage() {
        if (!driver.getCurrentUrl().contains("/login")) {
            throw new AssertionError(
                    "Expected URL to contain /login but was: " + driver.getCurrentUrl()
            );
        }
        return this;
    }

    /** Asserts the flash message is visible and contains the given text. */
    public LoginScreenState assertFlashContains(String expected) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(flashMessage));
        String actual = driver.findElement(flashMessage).getText().trim();
        if (!actual.contains(expected)) {
            throw new AssertionError(
                    "Flash expected to contain [" + expected + "] but was [" + actual + "]"
            );
        }
        return this;
    }

    /** Asserts the flash message is NOT currently visible. */
    public LoginScreenState assertNoFlash() {
        if (driver.findElements(flashMessage).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        })) {
            throw new AssertionError("Flash message should not be visible, but it is");
        }
        return this;
    }

    /** Asserts that the username input, password input, and button are all visible. */
    public LoginScreenState assertFormVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        return this;
    }

    /** Asserts the form heading text. */
    public LoginScreenState assertFormHeadingEquals(String expected) {
        String actual = driver.findElement(formHeading).getText().trim();
        if (!actual.equals(expected)) {
            throw new AssertionError(
                    "Form heading expected [" + expected + "] but was [" + actual + "]"
            );
        }
        return this;
    }

    // =========================================================================
    // Raw state readers
    // =========================================================================

    public String getFlashText() {
        return driver.findElement(flashMessage).getText().trim();
    }

    public boolean isFlashVisible() {
        try {
            return driver.findElement(flashMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
