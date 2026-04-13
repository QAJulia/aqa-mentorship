package com.week09.ui.base;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * BasePage – shared Selenide page base (Week 9 UI layer).
 *
 * Identical in purpose to the Week 8 BasePage but now lives in
 * org.example.ui.base (separate UI package) and adds SLF4J logging.
 *
 * All UI page objects extend this class.
 * No driver field, no wait field — Selenide handles both.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // =========================================================================
    // Core interaction helpers
    // =========================================================================

    protected SelenideElement waitForVisible(By locator) {
        return $(locator).shouldBe(visible);
    }

    protected void clickWhenReady(By locator) {
        $(locator).click();
    }

    protected void typeInto(By locator, String text) {
        $(locator).val(text);
    }

    protected String getText(By locator) {
        return $(locator).getText().trim();
    }

    protected ElementsCollection findAll(By locator) {
        return $$(locator);
    }

    protected boolean isDisplayed(By locator) {
        return $(locator).is(visible);
    }

    protected boolean isSelected(By locator) {
        return $(locator).is(checked);
    }

    protected void selectByVisibleText(By locator, String text) {
        $(locator).selectOption(text);
    }

    protected String getSelectedOption(By locator) {
        return $(locator).getSelectedOption().getText().trim();
    }

    // =========================================================================
    // Collection helpers
    // =========================================================================

    protected List<String> getAllTexts(By locator) {
        return $$(locator).texts();
    }

    protected int countElements(By locator) {
        return $$(locator).size();
    }

    protected ElementsCollection assertCollectionSize(By locator, int expected) {
        return $$(locator).shouldHave(CollectionCondition.size(expected));
    }

    // =========================================================================
    // Page-level utilities
    // =========================================================================

    public String getTitle() {
        return WebDriverRunner.getWebDriver().getTitle();
    }

    public String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}
