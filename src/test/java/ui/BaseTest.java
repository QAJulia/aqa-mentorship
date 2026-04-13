package ui;

import com.codeborne.selenide.Selenide;
import com.week06.utils.SelenideConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * BaseTest – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6 BaseTest had:
 *     @BeforeMethod: WebDriver driver = DriverFactory.createDriver()
 *     @AfterMethod:  driver.quit() + manual TakesScreenshot
 *
 *   Week 8 BaseTest has:
 *     @BeforeSuite:  SelenideConfig.init()  — configure once, register AllureSelenide
 *     @BeforeMethod: nothing — Selenide creates the browser on the first open() call
 *     @AfterMethod:  Selenide.closeWebDriver() — Selenide handles driver.quit() internally
 *
 * KEY DIFFERENCES:
 *
 *  1. No 'protected WebDriver driver' field — tests and pages never hold a driver reference.
 *     They call $(), $$(), open() as static methods from Selenide.
 *
 *  2. No manual screenshot code — AllureSelenide listener (registered in SelenideConfig)
 *     attaches a PNG to the Allure report automatically on every test failure.
 *
 *  3. BASE_URL is not a constant here — it is set in SelenideConfig.Configuration.baseUrl,
 *     so page classes use open("/login") shorthand without knowing the host.
 */
public abstract class BaseTest {

    /**
     * Runs once before the entire suite.
     * Configures Selenide (browser, timeout, baseUrl) and registers AllureSelenide.
     * Must run before any open() or $() call.
     */
    @BeforeSuite(alwaysRun = true)
    public void globalSetUp() {
        SelenideConfig.init();
    }

    /**
     * Runs before each @Test method.
     * In Selenide there is nothing to do here — the browser is opened lazily
     * by the first open() call inside the page object.
     *
     * Kept as a hook in case a subclass needs per-test setup (e.g. login state).
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Browser is created lazily by Selenide on the first open() call.
        // No driver initialisation needed here.
    }

    /**
     * Runs after each @Test method.
     * Closes the browser. Selenide internally calls driver.quit().
     * Screenshots on failure are handled automatically by AllureSelenide.
     *
     * Week 6 equivalent:
     *   if (result.getStatus() == FAILURE) attachScreenshot(...)
     *   driver.quit()
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
