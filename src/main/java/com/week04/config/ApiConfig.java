package com.week04.config;

public class ApiConfig {
    public static final String BASE_URI = "https://petstore.swagger.io/v2";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";

    // Endpoints
    public static final String PET_ENDPOINT = "/pet";
    public static final String PET_BY_ID_ENDPOINT = "/pet/{petId}";
    public static final String PET_FIND_BY_STATUS_ENDPOINT = "/pet/findByStatus";

    public static final String USER_ENDPOINT = "/user";
    public static final String USER_BY_USERNAME_ENDPOINT = "/user/{username}";
}
