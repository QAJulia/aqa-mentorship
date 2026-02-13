# Week 2 – testng.xml, Parameters, External Data

## Learning Objectives

By the end of this lesson, you will:
1. Master `testng.xml` structure and configuration
2. Understand how to pass parameters from XML to tests
3. Load test data from external sources (CSV, YAML)
4. Use DataProviders to supply multiple test cases
5. Learn Lombok annotations for clean POJOs
6. Distinguish between `@Parameters` and `@DataProvider`
7. Group and run tests by suite

---

## 1. Understanding testng.xml

### 1.1 What is testng.xml?

`testng.xml` is an XML configuration file that allows you to:
- Define test suites and groups
- Specify which classes/methods to run
- Pass parameters to tests
- Control parallel execution
- Configure listeners

### 1.2 Basic Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="My Suite" thread-count="1" parallel="false">
    <test name="My Test">
        <classes>
            <class name="com.example.MyTest"/>
        </classes>
    </test>
</suite>
```

**Key Elements:**
- `<suite>`: Top-level container for all tests
- `<test>`: Logical grouping of test classes
- `<classes>`: Specifies which test classes to run
- `<methods>`: Optional, run specific test methods
- `<groups>`: Optional, run tests by group

### 1.3 Attributes

| Attribute | Description | Example |
|-----------|-------------|---------|
| `name` | Identifier for the suite/test | `name="Smoke Tests"` |
| `parallel` | Run tests in parallel: `false`, `methods`, `classes`, `tests` | `parallel="methods"` |
| `thread-count` | Number of threads for parallel execution | `thread-count="4"` |
| `verbose` | Logging level (0-10, higher = more verbose) | `verbose="2"` |

---

## 2. Parameters in testng.xml

### 2.1 Using @Parameters

Parameters allow you to pass values from XML directly into test methods.

**XML Example:**
```xml
<test name="Browser Test">
    <parameter name="browser" value="Chrome"/>
    <parameter name="url" value="https://example.com"/>
    <classes>
        <class name="com.example.ParameterTest"/>
    </classes>
</test>
```

**Test Code:**
```java
@Test
@Parameters({"browser", "url"})
public void testWithParameters(String browser, String url) {
    System.out.println("Browser: " + browser);
    System.out.println("URL: " + url);
    Assert.assertNotNull(browser);
}
```

### 2.2 @Parameters vs @DataProvider

| Feature | @Parameters | @DataProvider |
|---------|------------|---------------|
| Data Source | XML file | Method returning Object[][] or List |
| Flexibility | Limited to XML | Highly flexible (read files, DB, etc.) |
| Scalability | Good for few test cases | Better for many test cases |
| Use Case | Configuration values | Multiple data-driven test cases |

**Example: @Parameters** (XML-based)
```xml
<parameter name="username" value="admin"/>
<parameter name="password" value="pass123"/>
```

**Example: @DataProvider** (Method-based)
```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        {"admin", "pass123"},
        {"user", "pass456"}
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password) {
    // Test logic
}
```

---

## 3. Lombok Annotations

### 3.1 Why Lombok?

Lombok reduces boilerplate code by generating getters, setters, constructors, equals, hashCode, and toString methods automatically.

### 3.2 Common Annotations

| Annotation | Purpose |
|-----------|---------|
| `@Data` | Generates getters, setters, equals, hashCode, toString, requiredArgsConstructor |
| `@Getter` | Generates getters for all fields |
| `@Setter` | Generates setters for all fields |
| `@NoArgsConstructor` | Generates a no-argument constructor |
| `@AllArgsConstructor` | Generates constructor with all fields as parameters |
| `@Builder` | Generates builder pattern for the class |
| `@ToString` | Generates toString method |
| `@EqualsAndHashCode` | Generates equals and hashCode methods |

### 3.3 Example: Person Class with Lombok

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    private String name;
    private int age;
    private String email;
}
```

**Generated Code (automatically):**
- Getters for all fields
- Setters for all fields
- No-argument constructor
- All-argument constructor
- Builder pattern: `Person.builder().name("John").age(30).build()`
- toString, equals, hashCode

---

## 4. Loading Test Data from External Files

### 4.1 CSV Format

**people.csv**
```csv
name,age,email
Robert Plant,77,robert.plant@example.com
Jimmy Page,82,jimmy.page@example.com
John Paul Jones,80,john.paul.jones@example.com
John Bonham,32,john.bonham@example.com
```

### 4.2 YAML Format

**people.yaml**
```yaml
people:
  - name: Robert Plant
    age: 77
    email: robert.plant@example.com
  - name: Jimmy Page
    age: 82
    email: jimmy.page@example.com
  - name: John Paul Jones
    age: 80
    email: john.paul.jones@example.com
  - name: John Bonham
    age: 32
    email: john.bonham@example.com
```

### 4.3 Reading CSV Files

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    public static List<Person> readPersonsFromCSV(String filePath) throws IOException {
        List<Person> persons = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Person person = Person.builder()
                    .name(parts[0])
                    .age(Integer.parseInt(parts[1]))
                    .email(parts[2])
                    .build();
                persons.add(person);
            }
        }
        return persons;
    }
}
```

### 4.4 Reading YAML Files

```java
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class YAMLReader {
    public static List<Person> readPersonsFromYAML(String filePath) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Map<String, List<Map<String, Object>>> data = yaml.load(fis);
            List<Map<String, Object>> peopleData = data.get("people");
            List<Person> persons = new ArrayList<>();
            
            for (Map<String, Object> personData : peopleData) {
                Person person = Person.builder()
                    .name((String) personData.get("name"))
                    .age((Integer) personData.get("age"))
                    .email((String) personData.get("email"))
                    .build();
                persons.add(person);
            }
            return persons;
        }
    }
}
```

---

## 5. DataProviders with External Data

### 5.1 Single DataProvider from CSV

```java
@DataProvider(name = "peopleFromCSV")
public Object[][] getPeopleFromCSV() throws IOException {
    List<Person> persons = CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv");
    Object[][] data = new Object[persons.size()][1];
    for (int i = 0; i < persons.size(); i++) {
        data[i][0] = persons.get(i);
    }
    return data;
}

@Test(dataProvider = "peopleFromCSV")
public void testPeopleFromCSV(Person person) {
    System.out.println("Testing: " + person);
    Assert.assertNotNull(person.getName());
    Assert.assertTrue(person.getAge() > 0);
}
```

### 5.2 Combining Data from Multiple Sources

```java
@DataProvider(name = "combinedPeopleData")
public Object[][] getCombinedPeopleData() throws IOException {
    List<Person> csvPersons = CSVReader.readPersonsFromCSV("src/test/resources/lesson02/people.csv");
    List<Person> yamlPersons = YAMLReader.readPersonsFromYAML("src/test/resources/lesson02/people.yaml");
    
    List<Person> allPersons = new ArrayList<>();
    allPersons.addAll(csvPersons);
    allPersons.addAll(yamlPersons);
    
    Object[][] data = new Object[allPersons.size()][1];
    for (int i = 0; i < allPersons.size(); i++) {
        data[i][0] = allPersons.get(i);
    }
    return data;
}

@Test(dataProvider = "combinedPeopleData")
public void testCombinedData(Person person) {
    System.out.println("Person: " + person.getName() + ", Age: " + person.getAge());
    Assert.assertNotNull(person.getName());
}
```

---

## 6. Test Grouping in testng.xml

### 6.1 Using @Test Groups

```java
@Test(groups = "smoke")
public void smokeTest1() { }

@Test(groups = "regression")
public void regressionTest1() { }

@Test(groups = {"smoke", "critical"})
public void criticalTest() { }
```

### 6.2 Running by Groups in XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="Grouped Suite">
    <test name="Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.example.PeopleTests"/>
        </classes>
    </test>
</suite>
```

---

## 7. Running Tests from Command Line

### 7.1 Run Specific testng.xml

```bash
mvn test -DsuiteXmlFile=testng.xml
```

### 7.2 Run Specific Test Class

```bash
mvn test -Dtest=PeopleTests
```

### 7.3 Run Specific Test Method

```bash
mvn test -Dtest=PeopleTests#testCombinedPersonData
```

### 7.4 Run by Group

```bash
mvn test -Dgroups=smoke
```

---

## 8. Best Practices

1. **Keep external data separate**: Store CSV/YAML in `src/test/resources/`
2. **Use descriptive names**: DataProvider names should reflect the data source
3. **Handle exceptions properly**: IOException should be declared or wrapped
4. **Combine data sources wisely**: Use when it makes logical sense
5. **Group tests by priority**: Smoke, regression, critical, etc.
6. **Parameterize configuration**: Use XML parameters for environment-specific values
7. **Document your structure**: Keep testng.xml readable with comments

---

## 9. Summary Table

| Concept | Purpose | When to Use |
|---------|---------|------------|
| `testng.xml` | Configure test execution | Always, for complex test runs |
| `@Parameters` | Pass XML values to tests | Configuration values, environment settings |
| `@DataProvider` | Supply multiple test cases | Data-driven testing from any source |
| Lombok | Reduce boilerplate | Always, for POJOs and models |
| CSV | External test data | Spreadsheet-compatible data |
| YAML | External test data | Hierarchical, human-readable data |
| Groups | Organize tests logically | Always, for test categorization |

---

## Resources

- [TestNG Documentation](https://testng.io/)
- [Lombok Project](https://projectlombok.org/)
- [SnakeYAML Documentation](https://bitbucket.org/asomov/snakeyaml/wiki/Home)

