package com.week10.api.spec;

import com.week10.config.AppConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.week10.api.filter.CorrelationIdFilter;
import com.week10.api.filter.TimingFilter;

/**
 * RequestSpecFactory – factory for shared REST Assured RequestSpecification objects.
 *
 * PROBLEM it solves:
 *   In Week 9 each API client built its own RequestSpecBuilder inline.
 *   If the base URL, headers, or content type change, you update N clients.
 *
 * SOLUTION:
 *   One factory produces typed specs. All clients call the factory.
 *   Changing the base URL or adding an auth header is a one-line edit here.
 *
 * PATTERN: Factory Method
 *   - defaultSpec()        → base URL, JSON content type, Allure filter, custom filters
 *   - authenticatedSpec()  → same as default + Authorization header placeholder
 *   - minimalSpec()        → no filters, no logging (for negative/performance tests)
 *
 * USAGE in clients:
 *   private final RequestSpecification spec = RequestSpecFactory.defaultSpec();
 *
 *   RestAssured.given(spec)
 *              .get("/BookStore/v1/Books")
 *              ...
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {}

    /**
     * Default spec used by all standard API clients.
     *
     * Includes:
     *   - base URI from AppConfig
     *   - Accept: application/json
     *   - Content-Type: application/json
     *   - AllureRestAssured filter   → attaches request/response to report
     *   - TimingFilter               → logs response time in Allure
     *   - CorrelationIdFilter        → adds X-Correlation-ID header for traceability
     *   - Logs request line on failure (LogDetail.URI)
     */
    public static RequestSpecification defaultSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(AppConfig.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new TimingFilter())
                .addFilter(new CorrelationIdFilter())
                .log(LogDetail.URI)
                .build();
    }

    /**
     * Authenticated spec – adds a Bearer token header.
     * Token is read from the system property -Dapi.token or from AppConfig.
     *
     * Used as a placeholder to show WHERE authentication would go.
     * DemoQA BookStore public endpoints don't require a token for GET.
     */
    public static RequestSpecification authenticatedSpec(String bearerToken) {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultSpec())
                .addHeader("Authorization", "Bearer " + bearerToken)
                .build();
    }

    /**
     * Minimal spec – no filters, no Allure attachment.
     * Use for contract/performance tests where report verbosity is undesired.
     */
    public static RequestSpecification minimalSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(AppConfig.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}
