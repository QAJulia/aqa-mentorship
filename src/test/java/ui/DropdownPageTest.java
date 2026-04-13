package ui;

import com.week06.pages.DropdownPage;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * DropdownPageTest – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *   - new DropdownPage(driver).open(BASE_URL)  →  new DropdownPage().open()
 *   - selectByVisibleText(dropdown, text)      →  $(dropdown).selectOption(text)
 *   - getSelectedOption(dropdown)              →  $(dropdown).getSelectedOption().getText()
 *   - assertSelectedOption uses shouldHave(exactText(...))
 *
 * DataProvider and test structure are identical to Week 6.
 */
@Epic("Week 8 – Selenide")
@Feature("Dropdown Page")
public class DropdownPageTest extends BaseTest {

    @Test(description = "Page has exactly 2 selectable options",
          groups = {"smoke", "dropdown"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    public void dropdownHasTwoOptions() {
        new DropdownPage().open().assertOptionCount(2);
    }

    @Test(description = "Available options are 'Option 1' and 'Option 2'",
          groups = {"smoke", "dropdown"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    public void optionTextsAreCorrect() {
        List<String> options = new DropdownPage().open().getAvailableOptions();

        assertTrue(options.contains("Option 1"), "Option 1 should be present");
        assertTrue(options.contains("Option 2"), "Option 2 should be present");
    }

    @Test(description = "Selecting each option updates the visible selection",
          groups = {"regression", "dropdown"},
          dataProvider = "dropdownOptions")
    @Story("Selection behaviour")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "assertSelectedOption() uses $(dropdown).getSelectedOption().shouldHave(exactText(...)). " +
        "Replaces new Select(...).getFirstSelectedOption().getText() from Week 6."
    )
    public void selectingOptionUpdatesSelection(String optionText) {
        new DropdownPage()
                .open()
                .selectOption(optionText)
                .assertSelectedOption(optionText);
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
    public void pageHeadingContainsDropdown() {
        new DropdownPage().open().assertHeadingVisible();
    }
}
