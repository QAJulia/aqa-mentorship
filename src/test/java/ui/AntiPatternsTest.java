package ui;

import com.week07.antipatterns.LoginPageAntiPattern;
import com.week07.data.builders.UserBuilder;
import com.week07.data.model.User;
import com.week07.pages.fluent.FluentLoginPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * AntiPatternsTest – side-by-side comparison of bad vs good code.
 *
 * Each "bad" test is paired with a "good" equivalent.
 * Both perform the same assertion; the difference is in maintainability,
 * readability, and resilience.
 *
 * All tests in this class are in the "antipattern" group so they can be
 * run separately: mvn test -Dgroups=antipattern
 */
@Epic("Week 7 – Architecture & Patterns")
@Feature("Anti-Patterns vs Good Patterns")
public class AntiPatternsTest extends BaseTest {

    // =========================================================================
    // AP-1 + AP-2 + AP-3 + AP-5: everything wrong in one method
    // =========================================================================

    /**
     * ❌ BAD: raw driver calls in a test, hardcoded URL, hardcoded credentials,
     *         duplicated locators, Thread.sleep.
     *
     * This test WORKS but is fragile and hard to maintain:
     *  - If the base URL changes → update here AND everywhere else
     *  - If the button locator changes → find every occurrence manually
     *  - Thread.sleep(2000) is arbitrary; fails on slow CI, wastes time locally
     *  - The assertion string is a magic value with no named constant
     */
    @Test(description = "❌ ANTI-PATTERN: raw driver, sleep, hardcoded data",
          groups = {"antipattern"})
    @Story("Anti-pattern: raw driver + Thread.sleep + hardcoded data")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Intentionally bad test. Demonstrates AP-1 (business logic in test), " +
        "AP-2 (duplicated locators), AP-3 (Thread.sleep), AP-5 (magic strings). " +
        "Compare with the GOOD version below."
    )
    public void bad_loginWithRawDriverAndSleep() throws InterruptedException {
        // [AP-6] Hardcoded URL – not from config
        driver.get("https://the-internet.herokuapp.com/login");

        // [AP-2] Locator written inline, not stored as a named constant
        driver.findElement(org.openqa.selenium.By.id("username"))
              .sendKeys("tomsmith");                          // [AP-5] magic string

        driver.findElement(org.openqa.selenium.By.id("password"))
              .sendKeys("SuperSecretPassword!");             // [AP-5] magic string

        // [AP-3] Thread.sleep – fragile arbitrary delay
        Thread.sleep(1000);

        // [AP-2] Same locator written again (duplicated)
        driver.findElement(org.openqa.selenium.By.cssSelector("button.radius")).click();

        // [AP-3] Another sleep after click
        Thread.sleep(2000);

        String flash = driver.findElement(
                org.openqa.selenium.By.xpath("//div[starts-with(@id,'flash')]")
        ).getText();

        // [AP-1] Assertion inside the test with magic string – no context if it fails
        assertTrue(flash.contains("You logged into a secure area"));
    }

    /**
     * ✅ GOOD: same assertion, but via FluentLoginPage + UserBuilder.
     * - URL from TestConfig (configurable)
     * - Credentials from UserBuilder (one place to change)
     * - No locators in test code
     * - No Thread.sleep – explicit wait inside the page
     * - Assertion reads like English
     */
    @Test(description = "✅ GOOD: fluent chain + UserBuilder – same assertion, no anti-patterns",
          groups = {"antipattern", "smoke"})
    @Story("Good pattern: fluent + builder")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "The same login assertion as bad_loginWithRawDriverAndSleep, " +
        "written without any anti-patterns."
    )
    public void good_loginWithFluentPageAndBuilder() {
        User user = UserBuilder.validUser();

        new FluentLoginPage(driver)
                .open()
                .isPageOpened()
                .enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLogin()
                .assertFlashContains("You logged into a secure area");
    }

    // =========================================================================
    // AP via LoginPageAntiPattern helper class
    // =========================================================================

    /**
     * ❌ BAD: test delegates to LoginPageAntiPattern which has multiple anti-patterns
     *         baked in (see that class for [AP] labels).
     */
    @Test(description = "❌ ANTI-PATTERN: uses LoginPageAntiPattern class",
          groups = {"antipattern"})
    @Story("Anti-pattern: monolithic page method with embedded assertion")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "loginAndAssert() mixes navigation + interaction + assertion in one method. " +
        "It uses Thread.sleep, hardcoded URL, and throws RuntimeException on failure " +
        "instead of a proper TestNG assertion."
    )
    public void bad_monolithicPageMethod() {
        // The test looks clean but the anti-patterns are hidden inside LoginPageAntiPattern
        new LoginPageAntiPattern(driver)
                .loginAndAssert("tomsmith", "SuperSecretPassword!");
        // If this passes it only means the site responded within 3 seconds.
        // On a slow CI machine it will randomly fail.
    }
}
