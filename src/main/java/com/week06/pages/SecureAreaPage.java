package com.week06.pages;

import com.week06.base.BasePage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * SecureAreaPage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *   - Constructor no longer accepts WebDriver
 *   - isFlashDisplayed() uses $(By).is(visible) instead of try/catch findElement
 *   - assertWelcomeVisible() uses shouldBe(visible) — no TestNG assert needed
 */
public class SecureAreaPage extends BasePage {

    private final By flashMessage = By.xpath("//div[contains(@class,'flash')]");
    private final By heading      = By.xpath("//h2");
    private final By logoutLink   = By.xpath("//a[contains(text(),'Logout')]");

    // =========================================================================
    // Actions
    // =========================================================================

    public LoginPage clickLogout() {
        clickWhenReady(logoutLink);
        return new LoginPage();
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public String getFlashMessageText() {
        return getText(flashMessage);
    }

    public boolean isFlashDisplayed() {
        return isDisplayed(flashMessage);
    }

    public String getHeadingText() {
        return getText(heading);
    }

    // =========================================================================
    // Assertions
    // =========================================================================

    /**
     * Assert the welcome flash is visible.
     * shouldBe(visible) auto-waits and gives a clear failure message.
     */
    public SecureAreaPage assertWelcomeVisible() {
        $(flashMessage).shouldBe(visible);
        return this;
    }

    public SecureAreaPage assertFlashContains(String expected) {
        $(flashMessage).shouldHave(text(expected));
        return this;
    }

    public SecureAreaPage assertHeadingContains(String expected) {
        $(heading).shouldHave(text(expected));
        return this;
    }
}
