package com.week07.antipatterns;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPageAntiPattern – INTENTIONALLY BAD CODE for educational purposes.
 *
 * This class demonstrates common automation anti-patterns.
 * Each anti-pattern is labelled with [AP-N] so it can be cross-referenced
 * with WEEK_7_NOTES.md Part 3.
 *
 * ⚠️  DO NOT copy this style. Compare with LoginPage (Week 6) to see the fix.
 */
public class LoginPageAntiPattern {

    private final WebDriver driver;

    // [AP-6] No base class / no shared wait – boilerplate duplicated everywhere
    public LoginPageAntiPattern(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * [AP-1] Business logic in the "page" method.
     * This method builds the URL itself (hardcoded string),
     * performs the login steps, AND asserts the outcome –
     * mixing navigation, action, and assertion in one place.
     *
     * ✅ Fix: split into open() / enterUsername() / enterPassword() /
     *        clickLogin() and move assertion to the test or Screen object.
     */
    public void loginAndAssert(String username, String password) {
        // [AP-6] Hardcoded base URL – not configurable for different environments
        driver.get("https://the-internet.herokuapp.com/login");

        // [AP-2] Locator duplicated – the same XPath is repeated 3+ times
        //        across this class and in AntiPatternsTest
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);  // duplicate find
        driver.findElement(By.id("password")).sendKeys(password);

        // [AP-3] Thread.sleep – arbitrary hardcoded pause
        //        Fails on slow networks; wastes time on fast machines
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        driver.findElement(By.cssSelector("button.radius")).click();

        // [AP-3] Another sleep after click – explicit wait should be used
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // [AP-1] Assertion inside the page object – breaks separation of concerns
        String flash = driver.findElement(By.xpath("//div[starts-with(@id,'flash')]")).getText();
        if (!flash.contains("You logged into a secure area")) {
            throw new RuntimeException("Login failed! Flash was: " + flash);
        }
    }

    /**
     * [AP-5] Hardcoded test data directly inside the method.
     * Changing credentials means modifying production page code.
     *
     * ✅ Fix: accept User object from a UserBuilder; keep credentials in data layer.
     */
    public void loginWithHardcodedCredentials() {
        // [AP-5] Magic strings baked into the page class
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button.radius")).click();
    }

    /**
     * [AP-2] Duplicated locators.
     * The same CSS selector appears here AND in loginWithHardcodedCredentials().
     * If the button class changes, you must update every occurrence manually.
     *
     * ✅ Fix: declare once as a private final field in the page class.
     */
    public void clickLoginButtonAgain() {
        // Same locator as above – if button class changes this breaks in N places
        driver.findElement(By.cssSelector("button.radius")).click();
    }
}
