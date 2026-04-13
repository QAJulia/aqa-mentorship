package ui;

import com.week07.data.builders.UserBuilder;
import com.week07.data.model.User;
import com.week07.pages.fluent.FluentLoginPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

/**
 * FluentLoginTest – demonstrates Fluent Page Object + UserBuilder.
 *
 * Key patterns shown:
 *  1. open().isPageOpened() guard before interaction
 *  2. Full fluent chain – no intermediate variable assignments in tests
 *  3. UserBuilder static factory methods for named test data presets
 *  4. Assertion methods on the page object (assertXxx) instead of testng Assert
 *     → the test reads like a user story, not a list of assertions
 */
@Epic("Week 7 – Architecture & Patterns")
@Feature("Fluent Login Page")
public class FluentLoginTest extends BaseTest {

    @Test(description = "Valid login – full fluent chain with UserBuilder",
          groups = {"smoke", "fluent"})
    @Story("Successful login – fluent style")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Demonstrates fluent chain: open → isPageOpened → enter credentials " +
        "(from UserBuilder) → clickLogin → isPageOpened → assertWelcomeVisible. " +
        "No driver calls, no magic strings, no intermediate variables."
    )
    public void validLoginFluentChain() {
        User user = UserBuilder.validUser();

        new FluentLoginPage(driver)
                .open()
                .isPageOpened()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLogin()
                .isPageOpened()
                .assertWelcomeVisible()
                .assertFlashContains("You logged into a secure area");
    }

    @Test(description = "Failed login – stays on login page, shows error flash",
          groups = {"regression", "fluent"})
    @Story("Failed login – fluent style")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Uses invalidPasswordUser() from UserBuilder. " +
        "clickLoginExpectingFailure() returns LoginPage so the chain continues " +
        "on the same page and asserts the error message."
    )
    public void invalidLoginShowsErrorFluentChain() {
        User user = UserBuilder.invalidPasswordUser();

        new FluentLoginPage(driver)
                .open()
                .isPageOpened()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLoginExpectingFailure()
                .assertFlashContains("Your password is invalid");
    }

    @Test(description = "Login form heading is 'Login Page' – page guard + assertion chain",
          groups = {"regression", "fluent"})
    @Story("Page content validation – fluent")
    @Severity(SeverityLevel.MINOR)
    @Description("isPageOpened() guard followed by assertHeadingEquals – ancestor XPath inside the page.")
    public void loginFormHeadingIsCorrect() {
        new FluentLoginPage(driver)
                .open()
                .isPageOpened()
                .assertHeadingEquals("Login Page");
    }

    @Test(description = "Full login → logout cycle using fluent chain",
          groups = {"smoke", "fluent"})
    @Story("Logout – fluent style")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Combines FluentLoginPage and FluentSecureAreaPage in one unbroken chain. " +
        "clickLogout() returns FluentLoginPage, then asserts the logout flash."
    )
    public void loginAndLogoutFluentChain() {
        User user = UserBuilder.validUser();

        new FluentLoginPage(driver)
                .open()
                .isPageOpened()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLogin()
                .assertWelcomeVisible()
                .clickLogout()
                .isPageOpened()
                .assertFlashContains("You logged out");
    }
}
