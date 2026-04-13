package com.week06.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.week06.base.BasePage;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * DropdownPage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6                                      Week 8
 *   ─────────────────────────────────────────   ─────────────────────────────────────
 *   new Select(waitForVisible(dropdown))       → $(dropdown).selectOption(text)
 *     .selectByVisibleText(text)
 *   new Select(waitForVisible(dropdown))       → $(dropdown).getSelectedOption().getText()
 *     .getFirstSelectedOption().getText()
 *   driver.findElements(enabledOptions).size() → $$(enabledOptions).size()
 *   stream().map(getText).collect(...)         → $$(enabledOptions).texts()
 *
 * Selenide's selectOption() and getSelectedOption() replace the
 * verbose java.util.Select wrapper entirely.
 */
public class DropdownPage extends BasePage {

    // =========================================================================
    // Locators – identical XPath to Week 6
    // =========================================================================

    private final By dropdown      = By.id("dropdown");
    private final By enabledOptions =
            By.xpath("//select[@id='dropdown']/option[not(@disabled)]");
    private final By heading       = By.xpath("//h3[contains(text(),'Dropdown')]");

    // =========================================================================
    // Navigation
    // =========================================================================

    public DropdownPage open() {
        Selenide.open("/dropdown");
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    /**
     * Select an option by visible text.
     *
     * Week 6: selectByVisibleText(dropdown, text) → new Select(...).selectByVisibleText(text)
     * Week 8: $(dropdown).selectOption(text)  — built-in, no Select wrapper needed
     */
    public DropdownPage selectOption(String visibleText) {
        $(dropdown).selectOption(visibleText);
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    /**
     * Week 6: getSelectedOption(dropdown) → new Select(...).getFirstSelectedOption().getText()
     * Week 8: $(dropdown).getSelectedOption().getText()
     */
    public String getSelectedOptionText() {
        return $(dropdown).getSelectedOption().getText().trim();
    }

    /**
     * Week 6: stream().map(WebElement::getText).collect(toList())
     * Week 8: $$(enabledOptions).texts() — ElementsCollection built-in
     */
    public List<String> getAvailableOptions() {
        return $$(enabledOptions).texts();
    }

    public int getEnabledOptionCount() {
        return $$(enabledOptions).size();
    }

    public String getHeading() {
        return getText(heading);
    }

    // =========================================================================
    // Assertions
    // =========================================================================

    public DropdownPage assertSelectedOption(String expected) {
        $(dropdown).getSelectedOption().shouldHave(exactText(expected));
        return this;
    }

    public DropdownPage assertOptionCount(int expected) {
        $$(enabledOptions).shouldHave(
                com.codeborne.selenide.CollectionCondition.size(expected));
        return this;
    }

    public DropdownPage assertHeadingVisible() {
        $(heading).shouldBe(visible);
        return this;
    }
}
