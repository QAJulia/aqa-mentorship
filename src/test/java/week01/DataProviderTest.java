package week01;

import com.week01.StringValidator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderTest {

    private StringValidator validator;

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

    @DataProvider(name = "palindromeData")
    public Object[][] palindromeDataProvider() {
        return new Object[][] {
                {"racecar", true},
                {"A man, a plan, a canal: Panama", true},
                {"hello", false},
                {"Madam", true},
                {"", false}
        };
    }

    @Test(dataProvider = "emailTestData")
    public void testEmailValidationWithDataProvider(String email, boolean expected) {
        validator = new StringValidator();
        boolean result = validator.isValidEmail(email);
        Assert.assertEquals(result, expected,
                "Email validation failed for: " + email);
    }

    @Test(dataProvider = "palindromeData")
    public void testPalindromeCheck(String input, boolean expected) {
        validator = new StringValidator();
        boolean result = validator.isPalindrome(input);
        Assert.assertEquals(result, expected);
    }
}
