package com.week07.pages.factory;

import com.week07.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * SecureAreaPageFactory – Page Factory style secure area page.
 * Returned by LoginPageFactory.clickLogin().
 */
public class SecureAreaPageFactory {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    @FindBy(xpath = "//div[contains(@class,'flash')]")
    private WebElement flashMessage;

    @FindBy(xpath = "//h2")
    private WebElement heading;

    @FindBy(linkText = "Logout")
    private WebElement logoutLink;

    public SecureAreaPageFactory(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeout()));
        PageFactory.initElements(driver, this);
    }

    // =========================================================================
    // Guard
    // =========================================================================

    public SecureAreaPageFactory isPageOpened() {
        wait.until(ExpectedConditions.urlContains("/secure"));
        wait.until(ExpectedConditions.visibilityOf(heading));
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public LoginPageFactory clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        return new LoginPageFactory(driver);
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
        wait.until(ExpectedConditions.visibilityOf(heading));
        return heading.getText().trim();
    }
}
