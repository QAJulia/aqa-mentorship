package com.week07.data.model;

/**
 * User – immutable domain object representing a login credential pair.
 *
 * LAYER RULE: Data model classes know nothing about pages, drivers, or tests.
 * They are plain Java objects (POJOs) that carry test data.
 *
 * Created via UserBuilder, never via direct constructor from tests.
 */
public class User {

    private final String username;
    private final String password;
    private final String displayName; // optional – what name appears after login

    /** Package-private: only UserBuilder calls this. */
    public User(String username, String password, String displayName) {
        this.username    = username;
        this.password    = password;
        this.displayName = displayName;
    }

    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() {
        return "User{username='" + username + "', displayName='" + displayName + "'}";
    }
}
