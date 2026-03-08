package com.week04.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON file and convert to object
     * @param filePath path to JSON file
     * @param clazz target class
     * @return object of type T
     */
    public static <T> T readJson(String filePath, Class<T> clazz) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return objectMapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    /**
     * Read JSON file as String
     * @param filePath path to JSON file
     * @return JSON as String
     */
    public static String readJsonAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    /**
     * Convert object to JSON string
     * @param object object to convert
     * @return JSON string
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
}
