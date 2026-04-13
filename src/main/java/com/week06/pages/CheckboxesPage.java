package com.week06.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import com.week06.base.BasePage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * CheckboxesPage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6                              Week 8
 *   ─────────────────────────────────   ─────────────────────────────────────
 *   driver.findElement(cb).click()    → $(cb).click()
 *   cb.isSelected()                   → $(cb).is(checked)
 *   List<WebElement> getAllCheckboxes → $$(allCheckboxes)   (ElementsCollection)
 *   .stream().filter(cb->!isSelected) → .filterBy(not(checked))
 *   .forEach(WebElement::click)       → .forEach(e -> e.click())
 *   driver.findElements(checked).size → $$(checkedBoxes).size()
 *
 * setSelected(true/false) is a Selenide convenience method that
 * checks OR unchecks a checkbox in one call, regardless of current state.
 * This replaces the "click only if not already in target state" pattern.
 */
public class CheckboxesPage extends BasePage {

    // =========================================================================
    // Locators – identical XPath to Week 6
    // =========================================================================

    private final By firstCheckbox = By.xpath("//form[@id='checkboxes']/input[1]");
    private final By lastCheckbox  = By.xpath("//form[@id='checkboxes']/input[last()]");
    private final By allCheckboxes = By.xpath("//form[@id='checkboxes']/descendant::input");
    private final By checkedBoxes  = By.xpath("//form[@id='checkboxes']/input[@checked]");
    private final By heading       = By.xpath("//h3[normalize-space()='Checkboxes']");

    // =========================================================================
    // Navigation
    // =========================================================================

    public CheckboxesPage open() {
        Selenide.open("/checkboxes");
        return this;
    }

    // =========================================================================
    // Actions
    // =========================================================================

    /**
     * Ensures the first checkbox is checked.
     *
     * Week 6: if (!isFirstChecked()) driver.findElement(firstCheckbox).click()
     * Week 8: setSelected(true) handles the state check internally
     */
    public CheckboxesPage checkFirst() {
        $(firstCheckbox).setSelected(true);
        return this;
    }

    /**
     * Ensures the last checkbox is unchecked.
     */
    public CheckboxesPage uncheckLast() {
        $(lastCheckbox).setSelected(false);
        return this;
    }

    /**
     * Ensures ALL checkboxes are checked.
     *
     * Week 6: stream + filter + forEach click
     * Week 8: filterBy(not(checked)).forEach(e -> e.setSelected(true))
     */
    public CheckboxesPage checkAll() {
        $$(allCheckboxes)
                .filterBy(not(checked))
                .forEach(e -> e.setSelected(true));
        return this;
    }

    /**
     * Ensures ALL checkboxes are unchecked.
     */
    public CheckboxesPage uncheckAll() {
        $$(allCheckboxes)
                .filterBy(checked)
                .forEach(e -> e.setSelected(false));
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public boolean isFirstChecked() {
        return $(firstCheckbox).is(checked);
    }

    public boolean isLastChecked() {
        return $(lastCheckbox).is(checked);
    }

    public int getTotalCheckboxCount() {
        return $$(allCheckboxes).size();
    }

    /**
     * Week 6 used driver.findElements(checkedBoxes).size().
     * Week 8: $$(checkedBoxes).size() — same XPath, simpler call.
     */
    public int getCheckedCount() {
        return $$(checkedBoxes).size();
    }

    public String getHeading() {
        return getText(heading);
    }

    // =========================================================================
    // Assertions – using Selenide shouldHave for clear failure messages
    // =========================================================================

    public CheckboxesPage assertTotalCount(int expected) {
        $$(allCheckboxes).shouldHave(CollectionCondition.size(expected));
        return this;
    }

    public CheckboxesPage assertFirstIsChecked() {
        $(firstCheckbox).shouldBe(checked);
        return this;
    }

    public CheckboxesPage assertLastIsChecked() {
        $(lastCheckbox).shouldBe(checked);
        return this;
    }
}
