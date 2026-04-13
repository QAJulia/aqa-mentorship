package com.week06.base;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * BasePage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6 BasePage:
 *
 *   Week 6 had:
 *     - protected WebDriver driver
 *     - protected WebDriverWait wait
 *     - Constructor accepting WebDriver
 *
 *   Week 8 has:
 *     - NO driver field  — Selenide manages the driver globally
 *     - NO wait field    — every $() call has an auto-wait built in
 *     - NO constructor parameter — page objects are instantiated with new XxxPage()
 *
 * METHOD MAPPING (Week 6 → Week 8):
 *
 *   waitForVisible(By)          →  $(By).shouldBe(visible)
 *   clickWhenReady(By)          →  $(By).click()
 *   typeInto(By, text)          →  $(By).val(text)
 *   getText(By)                 →  $(By).getText()
 *   findAll(By)                 →  $$(By)
 *   isDisplayed(By)             →  $(By).is(visible)
 *   isSelected(By)              →  $(By).is(checked)
 *   selectByVisibleText(By, t)  →  $(By).selectOption(t)
 *   getSelectedOption(By)       →  $(By).getSelectedOption().getText()
 */
public abstract class BasePage {

    // =========================================================================
    // Core interaction helpers
    // – Each method wraps a single Selenide call with a named intent.
    // – The explicit By parameter keeps the same signature as Week 6
    //   so page classes look identical from the outside.
    // =========================================================================

    /**
     * Wait until the element is visible, then return it.
     * Selenide's auto-wait fires before shouldBe() is evaluated.
     */
    protected SelenideElement waitForVisible(By locator) {
        return $(locator).shouldBe(visible);
    }

    /**
     * Click the element when it is clickable.
     * Selenide waits for the element to be visible+enabled before clicking.
     */
    protected void clickWhenReady(By locator) {
        $(locator).click();
    }

    /**
     * Clear the field and type the given text.
     * val() is Selenide's equivalent of clear() + sendKeys().
     */
    protected void typeInto(By locator, String text) {
        $(locator).val(text);
    }

    /**
     * Return the trimmed visible text of the element.
     * Selenide waits for the element to exist in DOM before getText().
     */
    protected String getText(By locator) {
        return $(locator).getText().trim();
    }

    /**
     * Return all matching elements as an ElementsCollection (Selenide list).
     */
    protected ElementsCollection findAll(By locator) {
        return $$(locator);
    }

    /**
     * Non-throwing visibility check.
     * Selenide's is() checks the current state without throwing.
     */
    protected boolean isDisplayed(By locator) {
        return $(locator).is(visible);
    }

    /**
     * Non-throwing selected/checked check.
     */
    protected boolean isSelected(By locator) {
        return $(locator).is(checked);
    }

    /**
     * Select a <select> option by its visible text.
     */
    protected void selectByVisibleText(By locator, String text) {
        $(locator).selectOption(text);
    }

    /**
     * Return the currently selected option text from a <select> element.
     */
    protected String getSelectedOption(By locator) {
        return $(locator).getSelectedOption().getText().trim();
    }

    // =========================================================================
    // Assertion helpers — return the element for optional chaining
    // =========================================================================

    /**
     * Assert the element is visible with the default timeout.
     */
    protected SelenideElement assertVisible(By locator) {
        return $(locator).shouldBe(visible);
    }

    /**
     * Assert the element's text contains the expected substring.
     */
    protected SelenideElement assertTextContains(By locator, String expected) {
        return $(locator).shouldHave(text(expected));
    }

    /**
     * Assert the collection has exactly the expected size.
     */
    protected ElementsCollection assertCollectionSize(By locator, int expectedSize) {
        return $$(locator).shouldHave(CollectionCondition.size(expectedSize));
    }

    // =========================================================================
    // Page-level utilities
    // =========================================================================

    public String getTitle() {
        return com.codeborne.selenide.WebDriverRunner.getWebDriver().getTitle();
    }

    public String getCurrentUrl() {
        return com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}
