package com.week07.pages.factory;

import com.week07.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TablesPageFactory – Page Factory version of TablesPage.
 *
 * Demonstrates:
 *  - @FindAll: collects ALL elements matching ANY of the given locators
 *    (useful for aggregating multiple element groups into one list)
 *  - @FindBy with XPath for list fields (List<WebElement>)
 *  - The key LIMITATION of Page Factory: dynamic XPath with parameters
 *    must still use driver.findElement(By.xpath(...)) directly
 *
 * Contrast with Classic TablesPage (Week 6):
 *  - Static locators → @FindBy   ✅ cleaner
 *  - Dynamic locators (rowByLastName) → still uses By.xpath(...)  ⚠️ must coexist
 */
public class TablesPageFactory {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // @FindBy on a List<WebElement> collects ALL matching elements
    @FindBy(xpath = "//table[@id='table1']//thead/tr/th")
    private List<WebElement> table1Headers;

    @FindBy(xpath = "//table[@id='table1']//tbody/tr")
    private List<WebElement> table1Rows;

    // @FindAll – all elements matching ANY of the listed @FindBy selectors
    // Here: collects both 'edit' and 'delete' action links in one list
    @FindAll({
        @FindBy(xpath = "//table[@id='table1']//td/a[text()='edit']"),
        @FindBy(xpath = "//table[@id='table1']//td/a[text()='delete']")
    })
    private List<WebElement> allActionLinks;

    @FindBy(xpath = "//table[@id='table1']//td/a[text()='edit']")
    private List<WebElement> editLinks;

    @FindBy(xpath = "//table[@id='table1']//td/a[text()='delete']")
    private List<WebElement> deleteLinks;

    // =========================================================================
    // Constructor
    // =========================================================================

    public TablesPageFactory(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeout()));
        PageFactory.initElements(driver, this);
    }

    // =========================================================================
    // Navigation + Guard
    // =========================================================================

    public TablesPageFactory open() {
        driver.get(TestConfig.getTablesUrl());
        return this;
    }

    public TablesPageFactory isPageOpened() {
        wait.until(ExpectedConditions.urlContains("/tables"));
        wait.until(ExpectedConditions.visibilityOfAllElements(table1Headers));
        return this;
    }

    // =========================================================================
    // Queries using @FindBy List fields
    // =========================================================================

    public List<String> getTable1Headers() {
        return table1Headers.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getRowCount() {
        return table1Rows.size();
    }

    public int getEditLinkCount()   { return editLinks.size(); }
    public int getDeleteLinkCount() { return deleteLinks.size(); }
    public int getAllActionLinkCount() { return allActionLinks.size(); }

    // =========================================================================
    // Dynamic locators – Page Factory CANNOT do this with annotations alone.
    // We fall back to explicit By.xpath() here to illustrate the limitation.
    // =========================================================================

    /**
     * Returns whether a row with the given last name exists.
     * Cannot be expressed as a @FindBy annotation because the last name
     * is a runtime value, not a compile-time constant.
     */
    public boolean hasRowWithLastName(String lastName) {
        String xpath = "//table[@id='table1']//tbody/tr" +
                       "[td[1][normalize-space()='" + lastName + "']]";
        return !driver.findElements(By.xpath(xpath)).isEmpty();
    }

    /**
     * Returns the email cell for the given last name row.
     * Uses following-sibling – must be built dynamically at runtime.
     */
    public String getEmailByLastName(String lastName) {
        String xpath = "//table[@id='table1']//tbody/tr/" +
                       "td[normalize-space()='" + lastName + "']/following-sibling::td[2]";
        WebElement cell = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))
        );
        return cell.getText().trim();
    }
}
