package ui;

import com.week06.pages.DropdownPage;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * DropdownPageTest – tests for https://the-internet.herokuapp.com/dropdown
 *
 * Demonstrates:
 *  - Selenium Select integration via BasePage helpers
 *  - XPath: //option[not(@disabled)] for filtering valid choices
 *  - DataProvider-driven test for each option
 *  - preceding-sibling axis usage (illustrated through the page object)
 */
@Epic("Week 6 – Page Object Model")
@Feature("Dropdown Page")
public class DropdownPageTest extends BaseTest {

    @Test(description = "Page has exactly 2 selectable options",
          groups = {"smoke", "dropdown"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the dropdown has 2 non-disabled options via XPath not(@disabled).")
    public void dropdownHasTwoOptions() {
        DropdownPage page = new DropdownPage(driver).open(BASE_URL);

        assertEquals(page.getEnabledOptionCount(), 2,
                "Expected 2 selectable options in the dropdown");
    }

    @Test(description = "Available options are 'Option 1' and 'Option 2'",
          groups = {"smoke", "dropdown"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the text values of all non-disabled dropdown options.")
    public void optionTextsAreCorrect() {
        DropdownPage page = new DropdownPage(driver).open(BASE_URL);

        List<String> options = page.getAvailableOptions();
        assertTrue(options.contains("Option 1"),
                "Option 1 should be present");
        assertTrue(options.contains("Option 2"),
                "Option 2 should be present");
    }

    @Test(description = "Selecting 'Option 1' updates the visible selection",
          groups = {"regression", "dropdown"},
          dataProvider = "dropdownOptions")
    @Story("Selection behaviour")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Select each option by visible text and verify it becomes the active selection.")
    public void selectingOptionUpdatesSelection(String optionText) {
        DropdownPage page = new DropdownPage(driver).open(BASE_URL);

        page.selectOption(optionText);

        assertEquals(page.getSelectedOptionText(), optionText,
                "Selected option should be: " + optionText);
    }

    @DataProvider(name = "dropdownOptions")
    public Object[][] dropdownOptions() {
        return new Object[][]{
                {"Option 1"},
                {"Option 2"}
        };
    }

    @Test(description = "Heading text contains 'Dropdown'",
          groups = {"smoke", "dropdown"})
    @Story("Page content")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the page heading using contains() XPath.")
    public void pageHeadingContainsDropdown() {
        DropdownPage page = new DropdownPage(driver).open(BASE_URL);

        assertTrue(page.getHeading().contains("Dropdown"),
                "Heading should contain 'Dropdown'");
    }
}
