package com.week06.pages;

import com.week06.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * SecureAreaPage – Page Object for https://the-internet.herokuapp.com/secure
 *
 * Returned by LoginPage.clickLogin() after a successful authentication.
 */
public class SecureAreaPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // The "You logged in..." flash banner
    private final By flashMessage = By.xpath("//div[contains(@class,'flash')]");

    // Page heading
    private final By heading = By.xpath("//h2");

    // Logout link – contains text "Logout"
    private final By logoutLink = By.xpath("//a[contains(text(),'Logout')]");

    // =========================================================================
    // Constructor
    // =========================================================================

    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public LoginPage clickLogout() {
        clickWhenReady(logoutLink);
        return new LoginPage(driver);
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
}
