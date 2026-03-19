package ui;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

/**
 * Tests for the Login page of the demo app.
 * URL: https://the-internet.herokuapp.com/login
 *
 * Valid credentials for this demo site:
 *   username: tomsmith
 *   password: SuperSecretPassword!
 *
 * This class extends BaseUiTest, so it gets a fresh WebDriver before each test
 * and the browser is closed after each test automatically.
 */
@Epic("UI Tests")
@Feature("Login")
public class LoginTest extends BaseUiTest {

    // Read the base URL once — all tests in this class use it
    private static final String BASE_URL = ConfigReader.get("base.url");

    // =========================================================================
    // Locators — defined as constants at the top so they are easy to find/update
    // In Week 6 we will move these into a Page Object class
    // =========================================================================
    private static final By USERNAME_INPUT  = By.id("username");
    private static final By PASSWORD_INPUT  = By.id("password");
    private static final By SUBMIT_BUTTON   = By.cssSelector("button[type='submit']");
    private static final By SUCCESS_MESSAGE = By.cssSelector(".flash.success");
    private static final By ERROR_MESSAGE   = By.cssSelector(".flash.error");
    private static final By LOGOUT_BUTTON   = By.cssSelector("a.button[href='/logout']");

    @Test
    @Story("Successful login")
    @Description("User logs in with valid credentials and sees a success message")
    @Severity(SeverityLevel.CRITICAL)
    public void testSuccessfulLogin() {
        // Arrange — open the login page
        driver.get(BASE_URL + "/login");

        // Act — fill in the form and submit
        driver.findElement(USERNAME_INPUT).sendKeys("tomsmith");
        driver.findElement(PASSWORD_INPUT).sendKeys("SuperSecretPassword!");
        driver.findElement(SUBMIT_BUTTON).click();

        // Assert — the success flash message is present
        String message = driver.findElement(SUCCESS_MESSAGE).getText();
        Assert.assertTrue(
                message.contains("You logged into a secure area!"),
                "Expected success message, but got: " + message
        );
    }

    @Test
    @Story("Failed login — wrong password")
    @Description("User enters wrong password and sees an error message")
    @Severity(SeverityLevel.NORMAL)
    public void testFailedLoginWrongPassword() {
        driver.get(BASE_URL + "/login");

        driver.findElement(USERNAME_INPUT).sendKeys("tomsmith");
        driver.findElement(PASSWORD_INPUT).sendKeys("wrongpassword");
        driver.findElement(SUBMIT_BUTTON).click();

        String message = driver.findElement(ERROR_MESSAGE).getText();
        Assert.assertTrue(
                message.contains("Your password is invalid!"),
                "Expected password error, but got: " + message
        );
    }

    @Test
    @Story("Failed login — wrong username")
    @Description("User enters a username that does not exist")
    @Severity(SeverityLevel.NORMAL)
    public void testFailedLoginWrongUsername() {
        driver.get(BASE_URL + "/login");

        driver.findElement(USERNAME_INPUT).sendKeys("unknownuser");
        driver.findElement(PASSWORD_INPUT).sendKeys("SuperSecretPassword!");
        driver.findElement(SUBMIT_BUTTON).click();

        String message = driver.findElement(ERROR_MESSAGE).getText();
        Assert.assertTrue(
                message.contains("Your username is invalid!"),
                "Expected username error, but got: " + message
        );
    }

    @Test
    @Story("Logout after successful login")
    @Description("User logs in and then logs out; should be redirected to login page")
    @Severity(SeverityLevel.NORMAL)
    public void testLogout() {
        // Step 1: log in
        driver.get(BASE_URL + "/login");
        driver.findElement(USERNAME_INPUT).sendKeys("tomsmith");
        driver.findElement(PASSWORD_INPUT).sendKeys("SuperSecretPassword!");
        driver.findElement(SUBMIT_BUTTON).click();

        // Step 2: click logout
        driver.findElement(LOGOUT_BUTTON).click();

        // Step 3: verify we are back on the login page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                currentUrl.contains("/login"),
                "Expected redirect to /login after logout, but URL was: " + currentUrl
        );
    }
}