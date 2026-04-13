package base;

import com.codeborne.selenide.Selenide;
import com.week09.utils.SelenideConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

/**
 * base.BaseUiTest – base class for all UI tests.
 *
 * Extends base.BaseTest (root) and adds:
 *   1. Selenide global configuration in @BeforeSuite
 *   2. Browser teardown after each test
 *
 * UI test classes extend base.BaseUiTest.
 * Unified API+UI test classes can extend base.BaseUiTest AND hold a BooksClient field
 * (they don't need to extend base.BaseApiTest because they only need the client, not the
 * full API base lifecycle — see BookStoreSyncTest for the pattern).
 *
 * LIFECYCLE ORDER (TestNG calls @BeforeSuite methods in inheritance order):
 *   1. base.BaseTest.logEnvironment()
 *   2. base.BaseUiTest.initUi()
 *   3. @BeforeMethod in the concrete test class
 *   4. @Test method
 *   5. base.BaseUiTest.tearDown()
 */
public abstract class BaseUiTest extends BaseTest {

    @BeforeSuite(alwaysRun = true, dependsOnMethods = "logEnvironment")
    public void initUi() {
        SelenideConfig.init();
        log.info("Selenide ready");
    }

    /**
     * Closes the browser after every test.
     * Selenide.closeWebDriver() calls driver.quit() internally.
     * AllureSelenide listener (registered in SelenideConfig) captures screenshots
     * automatically on failure — no manual screenshot code needed here.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
