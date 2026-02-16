package com.week03.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import com.week03.models.Person;
import com.week03.utils.CSVReader;
import com.week03.utils.YAMLReader;
import org.testng.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class containing Allure @Step methods for People-related operations.
 * Each method is a reusable step that appears in the Allure report.
 */
public class AllurePeopleSteps {

    @Step("Load all people from CSV and YAML files")
    public List<Person> loadAllPeople() throws IOException {
        List<Person> allPeople = new ArrayList<>();
        
        // Load from CSV
        List<Person> csvPeople = loadFromCSV();
        Allure.step("Loaded " + csvPeople.size() + " people from CSV");
        allPeople.addAll(csvPeople);
        
        // Load from YAML
        List<Person> yamlPeople = loadFromYAML();
        Allure.step("Loaded " + yamlPeople.size() + " people from YAML");
        allPeople.addAll(yamlPeople);
        
        Allure.step("Total people loaded: " + allPeople.size());
        return allPeople;
    }

    @Step("Load people from CSV file")
    private List<Person> loadFromCSV() throws IOException {
        return CSVReader.readPersonsFromCSV("src/test/resources/week03/testData/people.csv");
    }

    @Step("Load people from YAML file")
    private List<Person> loadFromYAML() throws IOException {
        return YAMLReader.readPersonsFromYAML("src/test/resources/week03/testData/people.yaml");
    }

    @Step("Calculate average age")
    public double calculateAverageAge(List<Person> people) {
        double average = people.stream()
                .mapToInt(Person::getAge)
                .average()
                .orElse(0.0);
        
        Allure.addAttachment("Average Age", "text/plain", 
                String.format("%.2f years", average));
        return average;
    }

    @Step("Verify average age {averageAge} is between {min} and {max}")
    public void verifyAverageAgeInRange(double averageAge, int min, int max) {
        Assert.assertTrue(averageAge >= min && averageAge <= max,
                String.format("Average age %.2f should be between %d and %d", 
                        averageAge, min, max));
    }

    @Step("Find oldest person")
    public Person findOldestPerson(List<Person> people) {
        Person oldest = people.stream()
                .max(Comparator.comparingInt(Person::getAge))
                .orElse(null);
        
        if (oldest != null) {
            Allure.step(String.format("Found oldest: %s, age %d", 
                    oldest.getName(), oldest.getAge()));
        }
        return oldest;
    }

    @Step("Find youngest person")
    public Person findYoungestPerson(List<Person> people) {
        Person youngest = people.stream()
                .min(Comparator.comparingInt(Person::getAge))
                .orElse(null);
        
        if (youngest != null) {
            Allure.step(String.format("Found youngest: %s, age %d", 
                    youngest.getName(), youngest.getAge()));
        }
        return youngest;
    }

    @Step("Verify person is valid")
    public void verifyPersonIsValid(Person person) {
        Assert.assertNotNull(person, "Person should not be null");
        Assert.assertNotNull(person.getName(), "Person name should not be null");
        Assert.assertTrue(person.getAge() > 0, "Person age should be positive");
        Assert.assertNotNull(person.getEmail(), "Person email should not be null");
    }

    @Step("Attach person details: {title}")
    public void attachPersonDetails(Person person, String title) {
        String details = String.format("""
            Name: %s
            Age: %d
            Email: %s
            """, person.getName(), person.getAge(), person.getEmail());
        
        Allure.addAttachment(title, "text/plain", details);
    }

    @Step("Count people under age {maxAge}")
    public int countPeopleUnderAge(List<Person> people, int maxAge) {
        int count = (int) people.stream()
                .filter(p -> p.getAge() < maxAge)
                .count();
        Allure.step("Found " + count + " people under " + maxAge);
        return count;
    }

    @Step("Count people between ages {minAge} and {maxAge}")
    public int countPeopleBetweenAges(List<Person> people, int minAge, int maxAge) {
        int count = (int) people.stream()
                .filter(p -> p.getAge() >= minAge && p.getAge() < maxAge)
                .count();
        Allure.step("Found " + count + " people between " + minAge + " and " + maxAge);
        return count;
    }

    @Step("Count people over age {minAge}")
    public int countPeopleOverAge(List<Person> people, int minAge) {
        int count = (int) people.stream()
                .filter(p -> p.getAge() >= minAge)
                .count();
        Allure.step("Found " + count + " people over " + minAge);
        return count;
    }

    @Step("Attach age distribution report")
    public void attachAgeDistributionReport(int under30, int between30And60, int over60) {
        int total = under30 + between30And60 + over60;
        
        String report = String.format("""
            ═══════════════════════════════════
            Age Distribution Report
            ═══════════════════════════════════
            Under 30:    %3d  (%.1f%%)
            30-60:       %3d  (%.1f%%)
            Over 60:     %3d  (%.1f%%)
            ───────────────────────────────────
            Total:       %3d  (100%%)
            ═══════════════════════════════════
            """,
            under30, (under30 * 100.0 / total),
            between30And60, (between30And60 * 100.0 / total),
            over60, (over60 * 100.0 / total),
            total
        );
        
        Allure.addAttachment("Age Distribution", "text/plain", report);
    }

    @Step("Verify email format for all people")
    public void verifyAllEmailsValid(List<Person> people) {
        for (Person person : people) {
            Assert.assertNotNull(person.getEmail(), 
                    "Email should not be null for: " + person.getName());
            Assert.assertTrue(person.getEmail().contains("@"), 
                    "Email should contain @ for: " + person.getName());
            Assert.assertTrue(person.getEmail().contains("."), 
                    "Email should contain . for: " + person.getName());
        }
        Allure.step("All " + people.size() + " emails are valid");
    }
}