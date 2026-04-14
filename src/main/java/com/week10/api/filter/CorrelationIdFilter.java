package com.week10.api.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * CorrelationIdFilter – injects a unique X-Correlation-ID header into every request.
 *
 * WHY?
 *   When a test fails in CI and you want to find the corresponding server log,
 *   you need a way to match the test's HTTP call to the backend log entry.
 *   A correlation ID (UUID per request) makes this possible.
 *
 *   Server-side, the application echoes the ID in its logs.
 *   Test-side, the ID appears in the Allure report (logged by SLF4J).
 *
 * REAL-WORLD USE:
 *   Most enterprise APIs already support X-Correlation-ID or X-Request-ID.
 *   This filter adds it automatically to every request without the test knowing.
 *
 * REGISTRATION:
 *   .addFilter(new CorrelationIdFilter())  in RequestSpecFactory.defaultSpec()
 */
public class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String HEADER_NAME = "X-Correlation-ID";

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        String correlationId = UUID.randomUUID().toString();

        // Mutate the request before it is sent
        requestSpec.header(HEADER_NAME, correlationId);

        log.debug("Sending {} {} with {}={}",
                requestSpec.getMethod(), requestSpec.getURI(),
                HEADER_NAME, correlationId);

        return ctx.next(requestSpec, responseSpec);
    }
}
