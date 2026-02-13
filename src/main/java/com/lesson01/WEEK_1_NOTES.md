# Week 01: TestNG Basics

This kind of documents covers the fundamentals of every week topic. In every week folder you will find similar 
NOTES document which will help you in your way to aqa position.

---

## Table of Contents
1. [Running Tests from Command Line](#running-tests-from-command-line)
2. [Project Structure](#project-structure)
3. [What We Have in Our Tests](#what-we-have-in-our-tests)
4. [TestNG Annotations (Hooks)](#testng-annotations-hooks)
5. [Data Providers](#data-providers)
6. [Assertions](#assertions)
7. [AAA Pattern](#aaa-pattern)
8. [Useful Resources](#useful-resources)

---

## Running Tests from Command Line

All commands must be run from the project root (where `pom.xml` is located).

### Basic Commands

```bash
# Run all tests
mvn test

# Clean target folder and run tests
mvn clean test

# Run a single test class
mvn -Dtest=StringValidatorTest test

# Run a single test method
mvn -Dtest=StringValidatorTest#testValidEmail test

# Run multiple classes
mvn -Dtest=StringValidatorTest,DataProviderTest test
```

### What Happens When You Run `mvn test`?

1. **Compilation**: Maven compiles `src/main/java` and `src/test/java`
2. **Test Discovery**: Surefire plugin finds all classes matching patterns: `*Test`, `*Tests`, `*TestCase`
3. **Test Execution**: All methods with `@Test` annotation are executed
4. **Report Generation**: Results are saved to `target/surefire-reports/`

---

## Project Structure

```
java-aqa-mentorship/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/lesson01/
│   │           └── StringValidator.java    # Production code
│   └── test/
│       └── java/
│           └── com/lesson01/
│               └── StringValidatorTest.java # Test code
│               └── DataProviderTest.java # Test code
├── pom.xml                                   # Maven configuration
└── target/                                   # Compiled classes, reports (generated)
```

**Key points:**
- Production code goes in `src/main/java`
- Test code goes in `src/test/java`
- Test classes should end with `Test`, `Tests`, or `TestCase`

---

## What We Have in Our Tests

### StringValidator Class (Production Code)

This is the class we're testing. It contains methods for string validation:

```java
public class StringValidator {
    
    // Validates email format using regex
    public boolean isValidEmail(String email) { ... }
    
    // Reverses a string
    public String reverseString(String input) { ... }
    
    // Checks if string is a palindrome
    public boolean isPalindrome(String input) { ... }
}
```

### StringValidatorTest Class (Test Code)

This class contains automated tests for `StringValidator`:

- **Setup method** (`@BeforeMethod`) - runs before each test
- **Teardown method** (`@AfterMethod`) - runs after each test
- **Test methods** (`@Test`) - actual test cases
- **Data providers** (`@DataProvider`) - supply test data for parameterized tests

---

## TestNG Annotations (Hooks)

Annotations control the **lifecycle** of test execution. They define when setup and teardown code runs.

### Common TestNG Annotations

| Annotation | Runs | Use Case |
|------------|------|----------|
| `@BeforeSuite` | Once before all tests in the suite | Setup shared resources (database connection, configuration) |
| `@AfterSuite` | Once after all tests in the suite | Cleanup shared resources |
| `@BeforeTest` | Before each `<test>` tag in testng.xml | Setup for test groups |
| `@AfterTest` | After each `<test>` tag in testng.xml | Cleanup for test groups |
| `@BeforeClass` | Once before the first test method in class | Initialize class-level resources |
| `@AfterClass` | Once after all test methods in class | Cleanup class-level resources |
| `@BeforeMethod` | Before each `@Test` method | Initialize fresh test data/objects |
| `@AfterMethod` | After each `@Test` method | Cleanup after each test |
| `@Test` | Actual test method | Contains test logic |

### Execution Order Example

```java
public class ExampleTest {
    
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("1. Before Suite - runs ONCE");
    }
    
    @BeforeClass
    public void beforeClass() {
        System.out.println("2. Before Class - runs ONCE per class");
    }
    
    @BeforeMethod
    public void beforeMethod() {
        System.out.println("3. Before Method - runs BEFORE EACH test");
    }
    
    @Test
    public void test1() {
        System.out.println("4. Test 1");
    }
    
    @AfterMethod
    public void afterMethod() {
        System.out.println("5. After Method - runs AFTER EACH test");
    }
    
    @Test
    public void test2() {
        System.out.println("4. Test 2");
    }
    
    @AfterClass
    public void afterClass() {
        System.out.println("6. After Class - runs ONCE per class");
    }
    
    @AfterSuite
    public void afterSuite() {
        System.out.println("7. After Suite - runs ONCE");
    }
}
```

**Output:**
```
1. Before Suite - runs ONCE
2. Before Class - runs ONCE per class
3. Before Method - runs BEFORE EACH test
4. Test 1
5. After Method - runs AFTER EACH test
3. Before Method - runs BEFORE EACH test
4. Test 2
5. After Method - runs AFTER EACH test
6. After Class - runs ONCE per class
7. After Suite - runs ONCE
```

### When to Use Which Annotation?

- **`@BeforeMethod` / `@AfterMethod`**: Most common. Use when each test needs fresh, isolated state.
- **`@BeforeClass` / `@AfterClass`**: When setup is expensive (e.g., loading large datasets, starting servers) and can be shared across tests.
- **`@BeforeSuite` / `@AfterSuite`**: Rarely used. For truly global setup (database connections, configuration loading).

---

## Data Providers

**Data Providers** allow you to run the same test logic with multiple sets of input data. This avoids code duplication.

### Without Data Provider (Bad - Duplication)

```java
@Test
public void testValidEmail1() {
    boolean result = validator.isValidEmail("test@example.com");
    Assert.assertTrue(result);
}

@Test
public void testValidEmail2() {
    boolean result = validator.isValidEmail("user@domain.co.uk");
    Assert.assertTrue(result);
}

@Test
public void testInvalidEmail1() {
    boolean result = validator.isValidEmail("invalid-email");
    Assert.assertFalse(result);
}
// ... 10 more similar tests
```

### With Data Provider (Good - No Duplication)

```java
@DataProvider(name = "emailTestData")
public Object[][] emailDataProvider() {
    return new Object[][] {
        {"test@example.com", true},
        {"user.name@domain.co.uk", true},
        {"invalid-email", false},
        {"@example.com", false},
        {"test@", false},
        {"", false},
        {null, false}
    };
}

@Test(dataProvider = "emailTestData")
public void testEmailValidation(String email, boolean expected) {
    boolean result = validator.isValidEmail(email);
    Assert.assertEquals(result, expected, "Email validation failed for: " + email);
}
```

### Data Provider Structure

```java
@DataProvider(name = "yourDataProviderName")
public Object[][] methodName() {
    return new Object[][] {
        {param1_test1, param2_test1, param3_test1}, // Test case 1
        {param1_test2, param2_test2, param3_test2}, // Test case 2
        {param1_test3, param2_test3, param3_test3}  // Test case 3
    };
}
```

- Returns **2D Object array**: `Object[][]`
- Each row = one test case
- Each column = one parameter for the test method
- Test method parameters must match the number of columns

### Connecting Data Provider to Test

```java
@Test(dataProvider = "yourDataProviderName")
public void testMethod(Type param1, Type param2, Type param3) {
    // Test logic using parameters
}
```

**Key Points:**
- Data provider name must match in `@Test(dataProvider = "name")`
- Test method parameters must match data provider columns (order and count)
- One test execution per row in the data provider

---

## Assertions

Assertions verify that actual results match expected results. If assertion fails, the test fails.

### Common TestNG Assertions

```java
import org.testng.Assert;

// Check equality
Assert.assertEquals(actual, expected);
Assert.assertEquals(actual, expected, "Custom error message");

// Boolean checks
Assert.assertTrue(condition);
Assert.assertFalse(condition);

// Null checks
Assert.assertNull(object);
Assert.assertNotNull(object);

// Fail test explicitly
Assert.fail("Test should not reach this point");
```

### Best Practice: Always Include Messages

```java
// ❌ Bad - unclear failure
Assert.assertTrue(result);

// ✅ Good - clear failure reason
Assert.assertTrue(result, "Email should be valid for: " + email);
```

---

## AAA Pattern

Every test should follow the **AAA pattern**:

1. **Arrange**: Set up test data and preconditions
2. **Act**: Execute the code being tested
3. **Assert**: Verify the result

### Example

```java
@Test
public void testPalindromeCheck() {
    // Arrange - prepare test data
    String input = "racecar";
    StringValidator validator = new StringValidator();
    
    // Act - execute the method
    boolean result = validator.isPalindrome(input);
    
    // Assert - verify the result
    Assert.assertTrue(result, "Should recognize 'racecar' as palindrome");
}
```

**Benefits:**
- Clear structure
- Easy to understand
- Easy to debug when tests fail

---

## Useful Resources

### Official Documentation
- [TestNG Official Documentation](https://testng.org/)
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) (for lesson 2)

### Tutorials
- [TestNG Tutorial - RUS](https://www.youtube.com/watch?v=xx0CYt03_bc)

### Tools
- [IntelliJ IDEA - Running Tests](https://www.jetbrains.com/help/idea/performing-tests.html)
- [MVN Repository - Find Dependencies](https://mvnrepository.com/)

---

## Quick Reference Card

```bash
# Maven commands
mvn test                                    # Run all tests
mvn clean test                              # Clean + run tests
mvn -Dtest=ClassName test                   # Run single class
mvn -Dtest=ClassName#methodName test        # Run single method

# TestNG annotations order
@BeforeSuite → @BeforeClass → @BeforeMethod → @Test → @AfterMethod → @AfterClass → @AfterSuite

# Common assertions
Assert.assertEquals(actual, expected);
Assert.assertTrue(condition);
Assert.assertFalse(condition);
Assert.assertNotNull(object);
```

---

**Next Steps:** Complete the homework in `TASKS.md` to practice these concepts!
