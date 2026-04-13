package ui;

import com.week06.pages.LoginPage;
import com.week06.pages.SecureAreaPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * LoginPageTest – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6                                   Week 8
 *   ──────────────────────────────────────   ──────────────────────────────────────
 *   new LoginPage(driver).open(BASE_URL)    → new LoginPage().open()
 *   assertTrue(secure.isFlashDisplayed())   → secure.assertWelcomeVisible()  (Selenide)
 *   assertTrue(flash.contains("..."))       → page.assertFlashContains("...")
 *   assertEquals(heading, "Login Page")     → kept as TestNG assertEquals
 *                                             (or could use .shouldHave(exactText))
 *
 * The test methods are IDENTICAL in structure to Week 6.
 * The only visible change: no 'driver' anywhere in this file.
 */
@Epic("Week 8 – Selenide")
@Feature("Login Page")
public class LoginPageTest extends BaseTest {

    private static final String VALID_USER = "tomsmith";
    private static final String VALID_PASS = "SuperSecretPassword!";

    @Test(description = "Valid login should display secure area with welcome message",
          groups = {"smoke", "login"})
    @Story("Successful authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Week 6→8 migration: same test, no WebDriver reference. " +
        "isFlashDisplayed() replaced with Selenide shouldBe(visible) via assertWelcomeVisible()."
    )
    public void validLoginShowsSecureArea() {
        // Week 6: new LoginPage(driver).open(BASE_URL)
        // Week 8: no driver, no BASE_URL — SelenideConfig.baseUrl handles it
        SecureAreaPage securePage = new LoginPage()
                .open()
                .enterUsername(VALID_USER)
                .enterPassword(VALID_PASS)
                .clickLogin();

        // Selenide assertion — auto-waits for flash to appear
        securePage.assertWelcomeVisible();
        securePage.assertFlashContains("You logged into a secure area");
        securePage.assertHeadingContains("Secure Area");
    }

    @Test(description = "Invalid password should show error flash message",
          groups = {"regression", "login"})
    @Story("Failed authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Wrong password → flash should contain 'Your password is invalid'.")
    public void invalidPasswordShowsError() {
        // clickLogin() returns SecureAreaPage structurally, but because login fails,
        // the driver stays on /login. We then use assertFlashContains on the
        // LoginPage directly — note: in real projects add clickLoginExpectingFailure().
        new LoginPage()
                .open()
                .enterUsername(VALID_USER)
                .enterPassword("wrongPassword")
                .clickLogin()
                .assertFlashContains("Your password is invalid");
    }

    @Test(description = "Login form heading should be 'Login Page'",
          groups = {"regression", "login"})
    @Story("Page content")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the ancestor-XPath heading text.")
    public void loginFormHeadingIsCorrect() {
        String heading = new LoginPage()
                .open()
                .getLoginFormHeading();

        assertEquals(heading, "Login Page", "Login form heading mismatch");
    }

    @Test(description = "User can log out after logging in",
          groups = {"smoke", "login"})
    @Story("Logout flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login → logout → assert 'You logged out' flash.")
    public void logoutReturnToLoginPage() {
        LoginPage afterLogout = new LoginPage()
                .open()
                .enterUsername(VALID_USER)
                .enterPassword(VALID_PASS)
                .clickLogin()
                .clickLogout();

        afterLogout.assertFlashContains("You logged out");
    }
}
