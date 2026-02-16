package week03;

import io.qameta.allure.*;
import com.week03.models.Person;
import com.week03.steps.AllurePeopleSteps;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

/**
 * Test class demonstrating Allure reporting with TestNG.
 * Uses @Epic, @Feature, @Story annotations to organize tests.
 * Integrates with custom listeners for enhanced reporting.
 */
@Epic("People Data Management")
@Feature("People Statistics Calculation")
public class AllurePeopleTests {

    private AllurePeopleSteps steps;
    private List<Person> allPeople;

    @BeforeClass
    public void setUp() throws IOException {
        steps = new AllurePeopleSteps();
        allPeople = steps.loadAllPeople();
    }

    @DataProvider(name = "ageRanges")
    public Object[][] ageRangeProvider() {
        return new Object[][] {
                {0, 30, "Young"},
                {30, 60, "Middle-aged"},
                {60, 150, "Senior"}
        };
    }

    @Test(description = "Calculate and verify average age from combined data sources", 
          groups = {"smoke", "statistics"})
    @Story("Average age calculation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        This test loads people from CSV and YAML files,
        combines them, and calculates the average age.
        Expected: Average should be between 20 and 100 years.
    """)
    public void testAverageAgeWithAllure() {
        double averageAge = steps.calculateAverageAge(allPeople);
        steps.verifyAverageAgeInRange(averageAge, 20, 100);
    }

    @Test(description = "Find oldest person from all data sources", 
          groups = {"regression", "statistics"})
    @Story("Oldest person identification")
    @Severity(SeverityLevel.NORMAL)
    public void testFindOldestPerson() {
        Person oldest = steps.findOldestPerson(allPeople);
        steps.verifyPersonIsValid(oldest);
        steps.attachPersonDetails(oldest, "Oldest Person");
    }

    @Test(description = "Find youngest person from all data sources", 
          groups = {"regression", "statistics"})
    @Story("Youngest person identification")
    @Severity(SeverityLevel.NORMAL)
    public void testFindYoungestPerson() {
        Person youngest = steps.findYoungestPerson(allPeople);
        steps.verifyPersonIsValid(youngest);
        steps.attachPersonDetails(youngest, "Youngest Person");
    }

    @Test(description = "Count people by age group", groups = {"regression"})
    @Story("Age demographics")
    @Severity(SeverityLevel.MINOR)
    public void testAgeGroupDistribution() {
        int under30 = steps.countPeopleUnderAge(allPeople, 30);
        int between30And60 = steps.countPeopleBetweenAges(allPeople, 30, 60);
        int over60 = steps.countPeopleOverAge(allPeople, 60);
        
        steps.attachAgeDistributionReport(under30, between30And60, over60);
    }

    @Test(description = "Verify all people have valid email addresses", 
          groups = {"smoke", "validation"})
    @Story("Data validation")
    @Severity(SeverityLevel.NORMAL)
    public void testEmailValidation() {
        steps.verifyAllEmailsValid(allPeople);
    }


    @Test(dataProvider = "ageRanges", 
          description = "Verify people count in different age ranges", 
          groups = {"regression"})
    @Story("Age range analysis")
    @Severity(SeverityLevel.NORMAL)
    public void testPeopleCountByAgeRange(int minAge, int maxAge, String category) {
        Allure.step("Analyzing age category: " + category);
        
        int count = steps.countPeopleBetweenAges(allPeople, minAge, maxAge);
        
        Allure.addAttachment("Category Analysis", "text/plain",
                String.format("Category: %s\nAge Range: %d-%d\nCount: %d", 
                        category, minAge, maxAge, count));
    }
}