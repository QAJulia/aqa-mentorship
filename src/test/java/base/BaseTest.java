package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

/**
 * base.BaseTest – root of the test class hierarchy.
 *
 * ┌───────────────────────────────────────────────────────────────────┐
 * │  base.BaseTest                                                          │
 * │    @BeforeSuite: log the environment config                        │
 * │                                                                    │
 * │  ├── base.BaseApiTest                                                   │
 * │  │     @BeforeSuite: RestAssuredConfig.init()                      │
 * │  │     field: BooksClient booksClient                              │
 * │  │                                                                 │
 * │  ├── base.BaseUiTest                                                    │
 * │  │     @BeforeSuite: SelenideConfig.init()                         │
 * │  │     @AfterMethod:  Selenide.closeWebDriver()                    │
 * │  │                                                                 │
 * │  └── (unified tests extend BOTH via base.BaseUiTest + booksClient)      │
 * └───────────────────────────────────────────────────────────────────┘
 *
 * Every specialised base class calls super() so @BeforeSuite of base.BaseTest
 * always runs first.
 *
 * LAYER RULE:
 *   base.BaseTest knows NOTHING about drivers, HTTP clients, or pages.
 *   It only handles concerns shared by ALL tests: environment logging.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeSuite(alwaysRun = true)
    public void logEnvironment() {
        log.info("═══════════════════════════════════════");
        log.info("  Test suite starting");
        log.info("  UI base URL : {}", com.week09.config.AppConfig.getUiBaseUrl());
        log.info("  API base URL: {}", com.week09.config.AppConfig.getApiBaseUrl());
        log.info("  Browser     : {}", com.week09.config.AppConfig.getBrowser());
        log.info("  Headless    : {}", com.week09.config.AppConfig.isHeadless());
        log.info("═══════════════════════════════════════");
    }
}
