package com.week09.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.week09.config.AppConfig;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SelenideConfig – configures Selenide from AppConfig and registers AllureSelenide.
 *
 * Called once in BaseUiTest @BeforeSuite.
 * All values come from AppConfig (which reads config.properties + system properties).
 */
public final class SelenideConfig {

    private static final Logger log = LoggerFactory.getLogger(SelenideConfig.class);
    private static boolean configured = false;

    private SelenideConfig() {}

    public static void init() {
        if (configured) return;

        Configuration.browser     = AppConfig.getBrowser();
        Configuration.headless    = AppConfig.isHeadless();
        Configuration.baseUrl     = AppConfig.getUiBaseUrl();
        Configuration.timeout     = AppConfig.getTimeout();
        Configuration.browserSize = AppConfig.getBrowserSize();
        Configuration.reportsFolder = "target/selenide-reports";
        Configuration.savePageSource = false;

        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));

        log.info("Selenide configured: browser={}, headless={}, baseUrl={}",
                Configuration.browser, Configuration.headless, Configuration.baseUrl);

        configured = true;
    }
}
