package base;

import com.week10.api.client.BooksClient;
import com.week10.config.AppConfig;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * base.BaseApiTest – root base for all Week 10 API tests.
 *
 * Compared to Week 9 base.BaseApiTest:
 *   - REST Assured is configured globally once in @BeforeSuite
 *   - BooksClient is created fresh per test in @BeforeMethod (test isolation)
 *   - Logging is done via SLF4J / Logback (not slf4j-simple)
 *
 * All three test classes extend this base:
 *   BooksApiTest, SchemaValidationTest, RequestSpecTest
 */
public abstract class BaseApiTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected BooksClient booksClient;

    @BeforeSuite(alwaysRun = true)
    public void initRestAssured() {
        RestAssured.baseURI = AppConfig.getApiBaseUrl();

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2))
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());

        log.info("REST Assured initialised: baseURI={}", RestAssured.baseURI);
    }

    @BeforeMethod(alwaysRun = true)
    public void createClient() {
        booksClient = new BooksClient();
    }
}
