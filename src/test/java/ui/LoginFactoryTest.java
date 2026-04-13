package ui;

import com.week07.data.builders.UserBuilder;
import com.week07.data.model.User;
import com.week07.pages.factory.LoginPageFactory;
import com.week07.pages.factory.SecureAreaPageFactory;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * LoginFactoryTest – tests using the Page Factory style page objects.
 *
 * Compare with FluentLoginTest to see:
 *  - Page Factory (@FindBy) vs Classic By / Fluent
 *  - Same tests, slightly different page API
 *  - PageFactory proxies hide the driver.findElement() call behind WebElement fields
 *
 * Both tests pass – the choice between POM styles is architectural, not functional.
 */
@Epic("Week 7 – Architecture & Patterns")
@Feature("Login Page – Page Factory Style")
public class LoginFactoryTest extends BaseTest {

    @Test(description = "Valid login using Page Factory page object",
          groups = {"smoke", "factory"})
    @Story("Successful login – Page Factory style")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Uses LoginPageFactory (PageFactory / @FindBy style). " +
        "Demonstrates how @FindBy proxies behave the same as By.id() for simple locators."
    )
    public void validLoginWithPageFactory() {
        User user = UserBuilder.validUser();

        LoginPageFactory loginPage = new LoginPageFactory(driver)
                .open()
                .isPageOpened();

        SecureAreaPageFactory securePage = loginPage
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLogin();

        securePage.isPageOpened();

        assertTrue(securePage.isFlashVisible(), "Flash message should be visible");
        assertTrue(securePage.getFlashText().contains("You logged into a secure area"),
                "Flash should contain success text");
    }

    @Test(description = "Logout after Page Factory login returns to login page",
          groups = {"regression", "factory"})
    @Story("Logout – Page Factory style")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click logout and verify driver is back on /login via isPageOpened() guard.")
    public void logoutWithPageFactory() {
        User user = UserBuilder.validUser();

        LoginPageFactory backOnLogin = new LoginPageFactory(driver)
                .open()
                .isPageOpened()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLogin()
                .isPageOpened()
                .clickLogout();

        backOnLogin.isPageOpened();
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Should be back on /login after logout");
    }

    @Test(description = "Page Factory heading text matches 'Login Page'",
          groups = {"regression", "factory"})
    @Story("Page content – Page Factory")
    @Severity(SeverityLevel.MINOR)
    @Description("@FindBy on h4 element inside the form – heading text assertion.")
    public void headingTextWithPageFactory() {
        LoginPageFactory page = new LoginPageFactory(driver)
                .open()
                .isPageOpened();

        assertEquals(page.getHeadingText(), "Login Page",
                "Heading should be 'Login Page'");
    }
}
