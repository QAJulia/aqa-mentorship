package com.week07.pages;

import com.week07.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage – Page Object for https://the-internet.herokuapp.com/login
 *
 * Demonstrates:
 *  - Standard id/name-based locators
 *  - contains() for partial class match on the login button
 *  - ancestor axis to find the flash message container
 *  - Fluent API: methods return 'this' while on the same page,
 *    or return SecureAreaPage after a successful login
 */
public class LoginPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // Standard id-based selectors – fast, stable
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");

    // Advanced XPath: button whose class CONTAINS 'radius'
    // Use case: the class string may be "radius" or "radius large" –
    //            contains() handles both variants
    private final By loginButton = By.xpath("//button[contains(@class,'radius')]");

    // Advanced XPath: div whose id is 'flash' – the notification area
    // starts-with used here because the id is exactly 'flash', but this
    // pattern illustrates the function for ids like 'flash-error', 'flash-success'
    private final By flashMessage = By.xpath("//div[starts-with(@id,'flash')]");

    // Advanced XPath: find the 'h4' text inside the login form
    // ancestor axis navigates UP: from the input to its containing <div>
    private final By loginFormHeading =
            By.xpath("//input[@id='username']/ancestor::form/h4");

    // =========================================================================
    // Constructor
    // =========================================================================

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        return this;
    }

    // =========================================================================
    // Actions – return 'this' for fluent chaining on the same page
    // =========================================================================

    public LoginPage enterUsername(String username) {
        typeInto(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeInto(passwordInput, password);
        return this;
    }

    /**
     * Clicks the login button.
     * Returns SecureAreaPage because a successful login navigates there.
     * If you need to test a failed login, add a method that returns LoginPage.
     */
    public SecureAreaPage clickLogin() {
        clickWhenReady(loginButton);
        return new SecureAreaPage(driver);
    }

    // =========================================================================
    // Queries / getters – return data, not page objects
    // =========================================================================

    public String getFlashMessageText() {
        return getText(flashMessage);
    }

    public boolean isFlashMessageDisplayed() {
        return isDisplayed(flashMessage);
    }

    public String getLoginFormHeading() {
        return getText(loginFormHeading);
    }
}
