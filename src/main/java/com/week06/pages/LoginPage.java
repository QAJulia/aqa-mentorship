package com.week06.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.week06.base.BasePage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * LoginPage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6                           Week 8
 *   ─────────────────────────────    ────────────────────────────────────
 *   constructor(WebDriver driver)  → no-arg constructor (no driver field)
 *   open(String baseUrl)           → open("/login")   (uses Configuration.baseUrl)
 *   typeInto(By, text)             → $(By).val(text)  via BasePage helper
 *   clickWhenReady(By)             → $(By).click()    via BasePage helper
 *   getText(By)                    → $(By).getText()   via BasePage helper
 *   isDisplayed(By)                → $(By).is(visible) via BasePage helper
 *
 * All XPath expressions are IDENTICAL to Week 6 — only the interaction
 * layer underneath has changed.
 */
public class LoginPage extends BasePage {

    // =========================================================================
    // Locators – identical to Week 6
    // =========================================================================

    private final By usernameInput  = By.id("username");
    private final By passwordInput  = By.id("password");
    private final By loginButton    = By.xpath("//button[contains(@class,'radius')]");
    private final By flashMessage   = By.xpath("//div[starts-with(@id,'flash')]");
    private final By loginFormHeading =
            By.xpath("//input[@id='username']/ancestor::form/h4");

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Navigate to the login page.
     * Uses Configuration.baseUrl so no hardcoded URL here.
     *
     * Week 6: open(String baseUrl) → driver.get(baseUrl + "/login")
     * Week 8: open("/login")       → Selenide prepends Configuration.baseUrl
     */
    public LoginPage open() {
        Selenide.open("/login");
        return this;
    }

    // =========================================================================
    // Actions
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
     * Click the login button and return the SecureAreaPage.
     *
     * Week 6: returned new SecureAreaPage(driver)
     * Week 8: returned new SecureAreaPage() — no driver to pass
     */
    public SecureAreaPage clickLogin() {
        clickWhenReady(loginButton);
        return new SecureAreaPage();
    }

    // =========================================================================
    // Queries / assertions
    // =========================================================================

    /**
     * Wait for the flash message to appear and return its text.
     *
     * Week 6: used waitForVisible(flashMessage).getText()
     * Week 8: shouldBe(visible) triggers auto-wait, then getText()
     */
    public String getFlashMessageText() {
        return getText(flashMessage);
    }

    public boolean isFlashMessageDisplayed() {
        return isDisplayed(flashMessage);
    }

    public String getLoginFormHeading() {
        return getText(loginFormHeading);
    }

    /**
     * Assert the flash message contains the given text.
     * Returns this for optional chaining.
     *
     * NEW in Week 8: Selenide shouldHave(text(...)) gives a much cleaner
     * assertion failure message than TestNG assertEquals.
     */
    public LoginPage assertFlashContains(String expected) {
        $(flashMessage).shouldHave(text(expected));
        return this;
    }
}
