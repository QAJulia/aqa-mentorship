# Lesson 01: Tasks

Complete the following tasks to practice TestNG concepts: annotations, data providers, assertions, and the AAA pattern.
All tasks must be created in your own repo in a "week1" branch.

---

## Task 1: Create Calculator Class

Create a new class `Calculator` in `src/main/java/com/lesson01/Calculator.java` with the following methods:

```java
package com.week01;

public class Calculator {

    /**
     * Adds two numbers
     * @return sum of a and b
     */
    public int add(int a, int b) {
        // TODO: implement
    }

    /**
     * Subtracts b from a
     * @return difference of a and b
     */
    public int subtract(int a, int b) {
        // TODO: implement
    }

    /**
     * Multiplies two numbers
     * @return product of a and b
     */
    public int multiply(int a, int b) {
        // TODO: implement
    }

    /**
     * Divides a by b
     * @return quotient of a and b
     * @throws ArithmeticException if b is zero
     */
    public double divide(int a, int b) {
        // TODO: implement
        // Remember to throw ArithmeticException when b == 0
    }

    /**
     * Checks if number is even
     * @return true if number is even, false otherwise
     */
    public boolean isEven(int number) {
        // TODO: implement
    }
}
```

---

## Task 2: Create Test Class with All Hooks

Create `CalculatorTest` in `src/test/java/lesson01/CalculatorTest.java`.

### Requirements:

1. **Use ALL TestNG annotations** to understand execution order:
   - `@BeforeSuite` - print "=== Starting Calculator Test Suite ==="
   - `@AfterSuite` - print "=== Calculator Test Suite Completed ==="
   - `@BeforeClass` - print "Setting up Calculator test class" and initialize a counter variable
   - `@AfterClass` - print "Tests completed. Total tests run: [counter]"
   - `@BeforeMethod` - print "Preparing for test..." and initialize `Calculator` object
   - `@AfterMethod` - print "Test finished!" and increment counter

2. **Important**: Add `System.out.println()` statements to see the execution order in console output.

### Example structure:

```java
package lesson01;

import com.week01.Calculator;
import org.testng.Assert;
import org.testng.annotations.*;

public class CalculatorTest {

    private Calculator calculator;
    private static int testCounter = 0;

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("=== Starting Calculator Test Suite ===");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("=== Calculator Test Suite Completed ===");
    }

    @BeforeClass
    public void beforeClass() {
        System.out.println("Setting up Calculator test class");
        testCounter = 0;
    }

    @AfterClass
    public void afterClass() {
        System.out.println("Tests completed. Total tests run: " + testCounter);
    }

    @BeforeMethod
    public void setUp() {
        calculator = new Calculator();
        System.out.println("Preparing for test...");
    }

    @AfterMethod
    public void tearDown() {
        testCounter++;
        System.out.println("Test finished!");
    }

    // TODO: Add your test methods here
}
```

---

## Task 3: Write Tests with Data Providers

For **each method** in `Calculator`, create:
1. A Data Provider with at least **5 test cases**
2. A test method that uses the Data Provider

### Example for `add()` method:

```java
@DataProvider(name = "additionData")
public Object[][] additionDataProvider() {
    return new Object[][] {
        {5, 3, 8},           // positive + positive
        {-5, 3, -2},         // negative + positive
        {-5, -3, -8},        // negative + negative
        {0, 5, 5},           // zero + positive
        {100, -100, 0}       // result is zero
    };
}

@Test(dataProvider = "additionData")
public void testAddition(int a, int b, int expected) {
    // Arrange
    // (calculator already initialized in @BeforeMethod)
    
    // Act
    int result = calculator.add(a, b);
    
    // Assert
    Assert.assertEquals(result, expected, 
        String.format("Addition failed: %d + %d should equal %d", a, b, expected));
}
```

### Your Tasks:

Create Data Providers and tests for:

1. ✅ `testAddition` - with data provider (example above)
2. ⬜ `testSubtraction` - with data provider (5+ test cases)
3. ⬜ `testMultiplication` - with data provider (5+ test cases)
4. ⬜ `testDivision` - with data provider (5+ test cases, include decimals)
5. ⬜ `testIsEven` - with data provider (5+ test cases: positive, negative, zero)


---

## Task 4: Test Exception Handling

The `divide()` method should throw `ArithmeticException` when dividing by zero.

Write a test that verifies this exception is thrown:

```java
@Test(expectedExceptions = ArithmeticException.class)
public void testDivisionByZero() {
    // Arrange
    int a = 10;
    int b = 0;
    
    // Act - should throw exception
    calculator.divide(a, b);
    
    // No Assert needed - test passes if exception is thrown
}
```

**Alternative approach** (more control):

```java
@Test
public void testDivisionByZeroWithTryCatch() {
    // Arrange
    int a = 10;
    int b = 0;
    
    // Act & Assert
    try {
        calculator.divide(a, b);
        Assert.fail("Expected ArithmeticException was not thrown");
    } catch (ArithmeticException e) {
        // Test passes
        Assert.assertTrue(e.getMessage().contains("zero") || e.getMessage().contains("/ by zero"));
    }
}
```

Choose **one approach** and implement it.

---

## Task 5: Run Tests and Verify Output

1. Run all tests from command line:
   ```bash
   mvn clean test
   ```

2. Verify in console output:
   - All hooks execute in correct order
   - `@BeforeSuite` runs first
   - `@BeforeMethod` runs before each test
   - `@AfterMethod` runs after each test
   - `@AfterClass` shows correct test counter
   - `@AfterSuite` runs last

3. Check test results:
   - All tests should pass ✅
   - Look at `target/surefire-reports/` for detailed reports

---

## Expected Deliverables

### 1. Calculator.java
- All 5 methods implemented correctly
- `divide()` throws `ArithmeticException` for division by zero

### 2. CalculatorTest.java
- All 6 TestNG hooks implemented (`@BeforeSuite`, `@AfterSuite`, `@BeforeClass`, `@AfterClass`, `@BeforeMethod`, `@AfterMethod`)
- 5 Data Providers created (one for each method)
- 5 tests with Data Providers
- 1 test for exception handling
- All tests follow AAA pattern
- Meaningful assertion messages

### 3. Console Output
Take a screenshot or copy console output showing:
- Hook execution order
- All tests passing
- Test counter working correctly

---

## Bonus Challenges (Optional)

1. **Add more edge cases**: What happens with `Integer.MAX_VALUE + 1`? How does `multiply()` behave with large numbers?

2. **Create a second test class**: Create `CalculatorNegativeTest` with tests that verify invalid inputs or edge cases.

3. **Add logging**: Instead of `System.out.println()`, use a logging framework (research how to add it to `pom.xml`).

4. **TestNG groups**: Research `@Test(groups = "smoke")` and create separate groups for "positive" and "negative" tests.

5. **Investigate your project repo**: Get an access to your project aqa framework, copy it to local environment. 
Investigate pom.xml and tests folders structure (resources, tests, utilities), find the use of every dependency.

---

## Getting Help

If stuck:
1. Review `NOTES.md` - all concepts are explained there
2. Check existing `StringValidatorTest` for examples
3. Run `mvn test` frequently to catch errors early
4. Read TestNG error messages carefully - they usually tell you what's wrong

---

## Submission

1. Commit your code to Git:
   ```bash
   git add .
   git commit -m "Completed Lesson 01 homework"
   git push
   ```

2. Create a Pull Request with:
   - Title: "Lesson 01: Calculator Tests Implementation"
   - Description: Brief summary of what you implemented
   - Screenshot or paste of console output showing hooks and test results
