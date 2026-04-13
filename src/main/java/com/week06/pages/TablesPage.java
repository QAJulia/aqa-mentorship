package com.week06.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.week06.base.BasePage;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * TablesPage – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *
 *   Week 6                                  Week 8
 *   ─────────────────────────────────────   ───────────────────────────────────────
 *   driver.findElements(By) → List<WebEl>  → $$(By) → ElementsCollection
 *   .stream().map(getText)                 → .texts()   (built-in on ElementsCollection)
 *   driver.findElements(By).size()         → $$(By).size()
 *   !driver.findElements(By).isEmpty()     → $$(By).size() > 0
 *   driver.findElement(By).getText()       → $(By).getText()
 *   row.findElements(By.tagName("td"))     → row.$$(By.tagName("td"))
 *   clickWhenReady(By)                     → $(By).click()
 *
 * All XPath expressions are identical to Week 6 — navigation axes, normalize-space,
 * following-sibling, ancestor — none of those change when switching to Selenide.
 */
public class TablesPage extends BasePage {

    private static final String TABLE1 = "table1";

    // =========================================================================
    // Static locators – identical XPath to Week 6
    // =========================================================================

    private final By table1Headers =
            By.xpath("//table[@id='" + TABLE1 + "']//thead/tr/th");

    private final By table1Rows =
            By.xpath("//table[@id='" + TABLE1 + "']//tbody/tr");

    private final By allDeleteLinks =
            By.xpath("//table[@id='" + TABLE1 + "']//td/a[text()='delete']");

    private final By allEditLinks =
            By.xpath("//table[@id='" + TABLE1 + "']//td/a[text()='edit']");

    // =========================================================================
    // Navigation
    // =========================================================================

    public TablesPage open() {
        Selenide.open("/tables");
        return this;
    }

    // =========================================================================
    // Dynamic (parameterised) locators – identical XPath to Week 6
    // =========================================================================

    private By rowByLastName(String lastName) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//tbody/tr" +
                "[td[1][normalize-space()='" + lastName + "']]"
        );
    }

    private By emailCellByLastName(String lastName) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//tbody/tr/" +
                "td[normalize-space()='" + lastName + "']/following-sibling::td[2]"
        );
    }

    private By actionLinkByLastName(String lastName, String action) {
        return By.xpath(
                "//table[@id='" + TABLE1 + "']//td[normalize-space()='" + lastName + "']" +
                "/ancestor::tr/td[last()]/a[text()='" + action + "']"
        );
    }

    // =========================================================================
    // Actions
    // =========================================================================

    public TablesPage clickEditFor(String lastName) {
        $(actionLinkByLastName(lastName, "edit")).click();
        return this;
    }

    public TablesPage clickDeleteFor(String lastName) {
        $(actionLinkByLastName(lastName, "delete")).click();
        return this;
    }

    // =========================================================================
    // Queries
    // =========================================================================

    /**
     * Returns header text list.
     *
     * Week 6: stream().map(WebElement::getText).collect(...)
     * Week 8: ElementsCollection.texts() — built-in convenience method
     */
    public List<String> getTable1Headers() {
        return $$(table1Headers).texts();
    }

    public int getTable1RowCount() {
        return $$(table1Rows).size();
    }

    public boolean hasRowWithLastName(String lastName) {
        return $$(rowByLastName(lastName)).size() > 0;
    }

    public String getEmailByLastName(String lastName) {
        return getText(emailCellByLastName(lastName));
    }

    /**
     * Returns all cell texts for a row identified by last name.
     *
     * Week 6: row.findElements(By.tagName("td")).stream()...
     * Week 8: row.$$(By.tagName("td")).texts()
     *         $() on a SelenideElement searches WITHIN that element (scoped find)
     */
    public List<String> getRowCellsByLastName(String lastName) {
        SelenideElement row = $(rowByLastName(lastName)).shouldBe(visible);
        return row.$$(By.tagName("td")).texts();
    }

    public int getDeleteLinkCount() {
        return $$(allDeleteLinks).size();
    }

    public int getEditLinkCount() {
        return $$(allEditLinks).size();
    }

    // =========================================================================
    // Assertions
    // =========================================================================

    public TablesPage assertRowCount(int expected) {
        $$(table1Rows).shouldHave(CollectionCondition.size(expected));
        return this;
    }

    public TablesPage assertRowExists(String lastName) {
        $(rowByLastName(lastName)).shouldBe(visible);
        return this;
    }

    public TablesPage assertEmailForRow(String lastName, String expectedEmail) {
        $(emailCellByLastName(lastName)).shouldHave(exactText(expectedEmail));
        return this;
    }
}
