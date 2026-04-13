package com.week09.utils;

import com.week09.config.AppConfig;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RestAssuredConfig – global REST Assured setup.
 *
 * Called once in BaseApiTest @BeforeSuite.
 * Sets global defaults so every RequestSpecBuilder starts
 * with the correct base URI and Jackson mapper.
 */
public final class RestAssuredConfig {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredConfig.class);
    private static boolean configured = false;

    private RestAssuredConfig() {}

    public static void init() {
        if (configured) return;

        RestAssured.baseURI = AppConfig.getApiBaseUrl();

        // Use Jackson for JSON → POJO serialisation globally
        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2))
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

        log.info("REST Assured configured: baseURI={}", RestAssured.baseURI);
        configured = true;
    }
}
