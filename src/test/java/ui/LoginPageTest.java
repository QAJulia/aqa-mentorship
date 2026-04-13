package ui;

import com.week06.pages.LoginPage;
import com.week06.pages.SecureAreaPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * LoginPageTest – tests for https://the-internet.herokuapp.com/login
 *
 * Demonstrates the POM pattern:
 *  - No driver.findElement() calls – all interaction via LoginPage / SecureAreaPage
 *  - Allure annotations for readable report structure
 *  - Page chaining: LoginPage → SecureAreaPage → LoginPage (logout)
 */
@Epic("Week 6 – Page Object Model")
@Feature("Login Page")
public class LoginPageTest extends BaseTest {

    // Valid credentials for the-internet
    private static final String VALID_USER = "tomsmith";
    private static final String VALID_PASS = "SuperSecretPassword!";

    // =========================================================================
    // Tests
    // =========================================================================

    @Test(description = "Valid login should display secure area with welcome message",
          groups = {"smoke", "login"})
    @Story("Successful authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Enter valid credentials and verify the user lands on the secure area page.")
    public void validLoginShowsSecureArea() {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);

        SecureAreaPage securePage = loginPage
                .enterUsername(VALID_USER)
                .enterPassword(VALID_PASS)
                .clickLogin();

        // The flash message should contain the success text
        String flash = securePage.getFlashMessageText();
        assertTrue(securePage.isFlashDisplayed(), "Flash message should be visible");
        assertTrue(flash.contains("You logged into a secure area"),
                "Flash should contain success text, was: " + flash);

        // Heading should confirm we are on the secure page
        assertTrue(securePage.getHeadingText().contains("Secure Area"),
                "Heading should contain 'Secure Area'");
    }

    @Test(description = "Invalid password should show error flash message",
          groups = {"regression", "login"})
    @Story("Failed authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Enter a wrong password and verify the login page shows an error banner.")
    public void invalidPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);

        // Deliberately use wrong password – stay on LoginPage, do NOT navigate to SecureArea
        // We do NOT call clickLogin() which returns SecureAreaPage; instead we use the raw action
        loginPage.enterUsername(VALID_USER)
                 .enterPassword("wrongPassword");

        // Click the button manually through the page object without type inference
        // This requires an additional method in LoginPage – see loginAndExpectFailure()
        // For now we call the standard clickLogin() and come back to verify flash
        // (The site redirects to /login on failure, so SecureAreaPage.driver is on /login)
        SecureAreaPage result = loginPage.clickLogin(); // driver stays on /login because login failed

        // The site still shows LoginPage HTML; we read the flash from the same driver
        String flash = result.getFlashMessageText();
        assertTrue(flash.contains("Your password is invalid"),
                "Expected invalid password message, got: " + flash);
    }

    @Test(description = "Login form heading should be 'Login Page'",
          groups = {"regression", "login"})
    @Story("Page content")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the login form heading text using ancestor XPath axis.")
    public void loginFormHeadingIsCorrect() {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);

        String heading = loginPage.getLoginFormHeading();
        assertEquals(heading, "Login Page",
                "Login form heading mismatch");
    }

    @Test(description = "User can log out after logging in",
          groups = {"smoke", "login"})
    @Story("Logout flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Perform login then logout and verify the user returns to login page.")
    public void logoutReturnToLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);

        SecureAreaPage securePage = loginPage
                .enterUsername(VALID_USER)
                .enterPassword(VALID_PASS)
                .clickLogin();

        LoginPage afterLogout = securePage.clickLogout();

        // After logout the flash message should mention logging out
        String flash = afterLogout.getFlashMessageText();
        assertTrue(flash.contains("You logged out"),
                "Expected logout flash, was: " + flash);
    }
}
