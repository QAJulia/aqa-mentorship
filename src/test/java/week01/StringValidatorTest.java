package week01;

import com.week01.StringValidator;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StringValidatorTest {

    private StringValidator validator;

    @BeforeMethod
    public void setUp() {
        validator = new StringValidator();
        System.out.println("Setting up test...");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Test completed!");
    }

    @Test
    public void testValidEmail() {
        String email = "test@example.com";
        boolean result = validator.isValidEmail(email);
        Assert.assertTrue(result, "Email should be valid");
    }

    @Test
    public void testInvalidEmail() {
        String email = "invalid-email";
        boolean result = validator.isValidEmail(email);
        Assert.assertFalse(result, "Email should be invalid");
    }
}
