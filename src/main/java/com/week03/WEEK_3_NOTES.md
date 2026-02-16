# Week 3: Allure Reporting & TestNG Listeners

## Learning Objectives

By the end of this 
lesson, you will:
1. Add Allure reporting to your project
2. Use Allure annotations (@Epic, @Feature, @Story, @Severity, @Step)
3. Create TestNG listeners (ITestListener, ISuiteListener)
4. Generate HTML reports with test steps and attachments

---

## 1. Project Structure

Based on your current setup:

```
java-aqa-mentorship/
├── src/
│   ├── main/java/
│   │   └── com.week03/
│   │       ├── listeners/
│   │       │   ├── CustomTestListener.java
│   │       │   └── SuiteListener.java
│   │       ├── models/
│   │       │   └── Person.java
│   │       └── steps/
│   │           └── AllurePeopleSteps.java
│   └── test/
│       ├── java/week03/
│       │   ├── AllurePeopleTests.java
│       │   └── PeopleTests.java
│       └── resources/week03/
│           ├── testng.xml
│           └── testData/
│               ├── people.csv
│               └── people.yaml
└── pom.xml
```

---

## 2. Add Allure to pom.xml

### Step 1: Add Allure dependency

In `<dependencies>` section, add:

```xml
<!-- Allure TestNG Integration -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>2.25.0</version>
</dependency>
```

### Step 2: Update Surefire plugin

Replace your existing `maven-surefire-plugin` with:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/week03/testng.xml</suiteXmlFile>
        </suiteXmlFiles>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.jar"
        </argLine>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.20.1</version>
        </dependency>
    </dependencies>
</plugin>
```

## 3. Allure Annotations

### Business Hierarchy

```java
@Epic("People Management")           // Highest level
@Feature("Statistics")               // Feature within epic
@Story("Calculate average age")      // Specific scenario
```

### Test Metadata

```java
@Severity(SeverityLevel.CRITICAL)    // BLOCKER, CRITICAL, NORMAL, MINOR, TRIVIAL
@Description("Detailed description of what test does")
```

### Steps

```java
@Step("Load data from CSV file")
public List<Person> loadFromCSV() {
    // implementation
}
```

---

## 4. TestNG Listeners

### ITestListener - Test Execution Hooks

```java
package com.week03.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class CustomTestListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("▶ STARTING: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✓ PASSED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("✗ FAILED: " + result.getMethod().getMethodName());
    }
}
```

### ISuiteListener - Suite Execution Hooks

```java
package com.week03.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class SuiteListener implements ISuiteListener {
    
    @Override
    public void onStart(ISuite suite) {
        System.out.println("🚀 Starting Suite: " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("✅ Finished Suite: " + suite.getName());
    }
}
```

### Register Listeners in testng.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Week 3 Suite" verbose="2">
    
    <listeners>
        <listener class-name="com.week03.listeners.CustomTestListener"/>
        <listener class-name="com.week03.listeners.SuiteListener"/>
    </listeners>
    
    <test name="All Tests">
        <classes>
            <class name="week03.AllurePeopleTests"/>
        </classes>
    </test>
    
</suite>
```

---

## 5. Create Step Methods

```java
package com.week03.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import com.week03.models.Person;

import java.util.List;

public class AllurePeopleSteps {

    @Step("Calculate average age for {people.size} people")
    public double calculateAverageAge(List<Person> people) {
        double avg = people.stream()
            .mapToInt(Person::getAge)
            .average()
            .orElse(0.0);
        
        Allure.addAttachment("Average Age", String.format("%.2f years", avg));
        return avg;
    }

    @Step("Find oldest person")
    public Person findOldest(List<Person> people) {
        return people.stream()
            .max((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()))
            .orElse(null);
    }
}
```

---

## 6. Create Test Class with Allure

```java
package week03;

import io.qameta.allure.*;
import com.week03.models.Person;
import com.week03.steps.AllurePeopleSteps;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@Epic("People Management")
@Feature("Statistics")
public class AllurePeopleTests {

    private AllurePeopleSteps steps;
    private List<Person> people;

    @BeforeClass
    public void setUp() {
        steps = new AllurePeopleSteps();
        people = loadPeopleData(); // your loading logic
    }

    @Test(groups = "smoke")
    @Story("Average age calculation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Calculates average age and verifies it's in valid range")
    public void testAverageAge() {
        double avg = steps.calculateAverageAge(people);
        Assert.assertTrue(avg > 0 && avg < 150, "Average age should be realistic");
    }

    @Test(groups = "regression")
    @Story("Oldest person")
    @Severity(SeverityLevel.NORMAL)
    public void testFindOldest() {
        Person oldest = steps.findOldest(people);
        Assert.assertNotNull(oldest, "Should find oldest person");
        Assert.assertTrue(oldest.getAge() > 0, "Age should be positive");
    }
}
```

---

## 7. Run Tests and Generate Report

### Run tests

```bash
mvn clean test
```

Results saved to: `target/allure-results/`

### Generate report

```bash
allure serve target/allure-results
```

This opens report in browser automatically.

### Alternative: Generate static report

```bash
allure generate target/allure-results -o allure-report --clean
```

Then open: `allure-report/index.html`

---


## Resources

- [Allure Documentation](https://docs.qameta.io/allure/)
- [TestNG Listeners](https://testng.org/doc/documentation-main.html#testng-listeners)
- [Allure TestNG](https://docs.qameta.io/allure/#_testng)