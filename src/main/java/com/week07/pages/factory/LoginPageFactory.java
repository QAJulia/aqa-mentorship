package com.week07.pages.factory;

import com.week07.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginPageFactory – Page Factory style implementation of the login page.
 *
 * Contrast with LoginPage (Week 6 Classic POM):
 *  - Locators are declared as @FindBy annotations on WebElement fields
 *  - PageFactory.initElements(driver, this) wires the proxy at construction time
 *  - The proxy calls driver.findElement(...) each time the field is accessed
 *    (no stale element issue for simple single-element fields)
 *
 * Limitation vs Classic POM:
 *  - @FindBy annotations are compile-time constants – you CANNOT build
 *    dynamic XPath with parameters inside @FindBy
 *  - For parameterised locators (e.g. "find row by last name") you must
 *    fall back to driver.findElement(By.xpath(...)) directly
 *
 * NOTE: This class intentionally does NOT extend BasePage so the
 * difference is visible. In a real project you would make a BasePageFactory
 * that calls PageFactory.initElements and holds the wait.
 */
public class LoginPageFactory {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // =========================================================================
    // @FindBy – annotation-based locator declaration
    // =========================================================================

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    // @FindBy with CSS selector
    @FindBy(css = "button.radius")
    private WebElement loginButton;

    // @FindBy with XPath
    @FindBy(xpath = "//div[starts-with(@id,'flash')]")
    private WebElement flashMessage;

    // @FindBys – tries locators in order, returns the first match found
    // Use case: a button that could have one of two possible locators
    @FindBys({
        @FindBy(css = "button.radius"),
        @FindBy(xpath = "//button[@type='submit']")
    })
    private WebElement submitButtonFallback;

    @FindBy(xpath = "//h4[@class='subheader']")
    private WebElement loginHeading;

    // =========================================================================
    // Constructor – MUST call PageFactory.initElements
    // =========================================================================

    public LoginPageFactory(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeout()));
        // This single call wires ALL @FindBy fields with lazy proxies
        PageFactory.initElements(driver, this);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    public LoginPageFactory open() {
        driver.get(TestConfig.getLoginUrl());
        return this;
    }

    /**
     * isPageOpened() guard – same contract as FluentLoginPage.
     * Unlike the Classic POM, we wait for the WebElement proxy to be visible
     * rather than using a By locator.
     */
    public LoginPageFactory isPageOpened() {
        wait.until(ExpectedConditions.visibilityOf(loginButton));
        wait.until(driver -> driver.getCurrentUrl().contains("/login"));
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public LoginPageFactory enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        return this;
    }

    public LoginPageFactory enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    /**
     * Clicks the login button.
     * Returns a new SecureAreaPageFactory (the next page after login).
     */
    public SecureAreaPageFactory clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        return new SecureAreaPageFactory(driver);
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public String getFlashText() {
        wait.until(ExpectedConditions.visibilityOf(flashMessage));
        return flashMessage.getText().trim();
    }

    public boolean isFlashVisible() {
        try {
            return flashMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeadingText() {
        wait.until(ExpectedConditions.visibilityOf(loginHeading));
        return loginHeading.getText().trim();
    }
}
