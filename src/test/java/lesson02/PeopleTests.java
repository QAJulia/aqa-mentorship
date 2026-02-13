package lesson02;

import com.lesson02.models.Person;
import com.lesson02.utils.CSVReader;
import com.lesson02.utils.YAMLReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Week 2 Test Class - External Data, DataProviders, and Data Processing
 *
 * This class demonstrates:
 * - Loading test data from CSV and YAML files
 * - Using DataProviders with external data
 * - Processing and analyzing test data
 * - Using Lombok POJOs for data modeling
 * - Organizing tests with groups
 */
public class PeopleTests {

    // ============================================
    // DataProviders
    // ============================================

    /**
     * DataProvider that reads people data from CSV file.
     * Returns 4 people
     */
    @DataProvider(name = "peopleFromCSV")
    public Object[][] getPeopleFromCSV() throws IOException {
        List<Person> persons = CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv");
        Object[][] data = new Object[persons.size()][1];

        for (int i = 0; i < persons.size(); i++) {
            data[i][0] = persons.get(i);
        }

        return data;
    }

    /**
     * DataProvider that reads people data from YAML file.
     * Returns 4 people
     */
    @DataProvider(name = "peopleFromYAML")
    public Object[][] getPeopleFromYAML() throws IOException {
        List<Person> persons = YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml");
        Object[][] data = new Object[persons.size()][1];

        for (int i = 0; i < persons.size(); i++) {
            data[i][0] = persons.get(i);
        }

        return data;
    }

    /**
     * DataProvider that combines people data from both CSV and YAML files.
     * Returns 8 people total
     */
    @DataProvider(name = "combinedPeople")
    public Object[][] getCombinedPeopleData() throws IOException {
        List<Person> allPersons = new ArrayList<>();

        // Add CSV data
        allPersons.addAll(CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv"));

        // Add YAML data
        allPersons.addAll(YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml"));

        Object[][] data = new Object[allPersons.size()][1];

        for (int i = 0; i < allPersons.size(); i++) {
            data[i][0] = allPersons.get(i);
        }

        return data;
    }

    // ============================================
    // Test Methods - Data Validation
    // ============================================

    /**
     * Test individual CSV records using DataProvider
     * Group: smoke (quick validation tests)
     */
    @Test(dataProvider = "peopleFromCSV", groups = "smoke")
    public void testCSVPersonData(Person person) {
        System.out.println("Testing CSV person: " + person);

        // Validate that all fields are populated
        Assert.assertNotNull(person.getName(), "Name should not be null");
        Assert.assertFalse(person.getName().isEmpty(), "Name should not be empty");

        Assert.assertNotNull(person.getEmail(), "Email should not be null");
        Assert.assertFalse(person.getEmail().isEmpty(), "Email should not be empty");

        // Validate age is positive
        Assert.assertTrue(person.getAge() > 0, "Age should be positive");
        Assert.assertTrue(person.getAge() < 150, "Age should be realistic");

        System.out.println("✓ CSV person validation passed: " + person.getName());
    }

    /**
     * Test individual YAML records using DataProvider
     * Group: smoke (quick validation tests)
     */
    @Test(dataProvider = "peopleFromYAML", groups = "smoke")
    public void testYAMLPersonData(Person person) {
        System.out.println("Testing YAML person: " + person);

        // Validate that all fields are populated
        Assert.assertNotNull(person.getName(), "Name should not be null");
        Assert.assertFalse(person.getName().isEmpty(), "Name should not be empty");

        Assert.assertNotNull(person.getEmail(), "Email should not be null");
        Assert.assertFalse(person.getEmail().isEmpty(), "Email should not be empty");

        // Validate age is positive
        Assert.assertTrue(person.getAge() > 0, "Age should be positive");
        Assert.assertTrue(person.getAge() < 150, "Age should be realistic");

        System.out.println("✓ YAML person validation passed: " + person.getName());
    }

    // ============================================
    // Test Methods - Data Processing
    // ============================================

    /**
     * Test average age calculation from combined data
     * Group: regression (comprehensive tests)
     */
    @Test(groups = "regression")
    public void testAverageAgeCalculation() throws IOException {
        // Load all people from both sources
        List<Person> people = new ArrayList<>();
        people.addAll(CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv"));
        people.addAll(YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml"));

        // Calculate average age
        double averageAge = calculateAverageAge(people);

        System.out.println("Total people: " + people.size());
        System.out.println("Average age: " + averageAge);

        // Verify results
        Assert.assertTrue(averageAge > 0, "Average age should be positive");
        Assert.assertTrue(averageAge < 100, "Average age should be less than 100");
        Assert.assertTrue(people.size() == 8, "Should have 8 people total");

        System.out.println("✓ Average age calculation passed");
    }

    /**
     * Test finding the oldest person from combined data
     * Group: regression (comprehensive tests)
     */
    @Test(groups = "regression")
    public void testFindOldestPerson() throws IOException {
        // Load all people from both sources
        List<Person> people = new ArrayList<>();
        people.addAll(CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv"));
        people.addAll(YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml"));

        // Find oldest person
        Person oldest = findOldestPerson(people);

        System.out.println("Oldest person: " + oldest.getName() + ", Age: " + oldest.getAge());

        // Verify results
        Assert.assertNotNull(oldest, "Oldest person should not be null");
        Assert.assertTrue(oldest.getAge() > 0, "Age should be positive");

        // Jimmy Page should be oldest (age 82)
        Assert.assertEquals(oldest.getName(), "Jimmy Page", "Jimmy Page should be oldest");
        Assert.assertEquals(oldest.getAge(), 82, "Oldest person should be 45");

        System.out.println("✓ Oldest person detection passed");
    }

    /**
     * Test finding the youngest person from combined data
     * Group: regression (comprehensive tests)
     */
    @Test(groups = "regression")
    public void testFindYoungestPerson() throws IOException {
        // Load all people from both sources
        List<Person> people = new ArrayList<>();
        people.addAll(CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv"));
        people.addAll(YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml"));

        // Find youngest person
        Person youngest = findYoungestPerson(people);

        System.out.println("Youngest person: " + youngest.getName() + ", Age: " + youngest.getAge());

        // Verify results
        Assert.assertNotNull(youngest, "Youngest person should not be null");
        Assert.assertTrue(youngest.getAge() > 0, "Age should be positive");

        // John Bonham should be youngest (age 32)
        Assert.assertEquals(youngest.getName(), "John Bonham", "John Bonham should be youngest");
        Assert.assertEquals(youngest.getAge(), 32, "Youngest person should be 32");

        System.out.println("✓ Youngest person detection passed");
    }

    /**
     * Test data from combined sources
     * Demonstrates using combined DataProvider
     * Group: smoke (quick validation tests)
     */
    @Test(dataProvider = "combinedPeople", groups = "smoke")
    public void testCombinedPersonData(Person person) {
        System.out.println("Combined data person: " + person);

        // Validate that all fields are populated
        Assert.assertNotNull(person.getName(), "Name should not be null");
        Assert.assertTrue(person.getAge() > 0, "Age should be positive");
        Assert.assertNotNull(person.getEmail(), "Email should not be null");

        System.out.println("✓ Combined data validation passed: " + person.getName());
    }

    // ============================================
    // Helper Methods - Data Processing Logic
    // ============================================

    /**
     * Calculate average age of a list of people
     *
     * @param people List of Person objects
     * @return Average age as double
     */
    private static double calculateAverageAge(List<Person> people) {
        if (people == null || people.isEmpty()) {
            return 0;
        }

        int totalAge = 0;
        for (Person person : people) {
            totalAge += person.getAge();
        }

        return (double) totalAge / people.size();
    }

    /**
     * Find the oldest person in a list
     *
     * @param people List of Person objects
     * @return Person object with highest age
     */
    private static Person findOldestPerson(List<Person> people) {
        if (people == null || people.isEmpty()) {
            return null;
        }

        return people.stream()
                .max(Comparator.comparingInt(Person::getAge))
                .orElse(null);
    }

    /**
     * Find the youngest person in a list
     *
     * @param people List of Person objects
     * @return Person object with lowest age
     */
    private static Person findYoungestPerson(List<Person> people) {
        if (people == null || people.isEmpty()) {
            return null;
        }

        return people.stream()
                .min(Comparator.comparingInt(Person::getAge))
                .orElse(null);
    }
}
