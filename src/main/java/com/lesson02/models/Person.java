package com.lesson02.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person model class using Lombok annotations.
 *
 * Lombok automatically generates:
 * - Getters for all fields
 * - Setters for all fields (mutable fields)
 * - Constructor with all fields as parameters (via @AllArgsConstructor)
 * - No-argument constructor (via @NoArgsConstructor)
 * - toString(), equals(), and hashCode() methods (via @Data)
 * - Builder pattern (via @Builder)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    private String name;
    private int age;
    private String email;
}
