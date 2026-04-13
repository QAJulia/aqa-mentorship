package com.week07.data.builders;


import com.week07.data.model.User;

/**
 * UserBuilder – Builder pattern for constructing User test data objects.
 *
 * Two usage styles are supported:
 *
 * 1. Static factory methods (named presets – most common in tests):
 *    User user = UserBuilder.validUser();
 *    User user = UserBuilder.invalidPasswordUser();
 *
 * 2. Fluent builder (for custom variations):
 *    User user = new UserBuilder()
 *            .withUsername("custom")
 *            .withPassword("pass")
 *            .withDisplayName("Custom User")
 *            .build();
 *
 * LAYER RULE: Builders live in the data layer. They never call
 * driver, page objects, or TestNG methods.
 */
public class UserBuilder {

    // Default values – override per test as needed
    private String username    = "tomsmith";
    private String password    = "SuperSecretPassword!";
    private String displayName = "Tom Smith";

    // =========================================================================
    // Fluent setters
    // =========================================================================

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public User build() {
        return new User(username, password, displayName);
    }

    // =========================================================================
    // Static factory methods – named presets
    // =========================================================================

    /**
     * A user with valid credentials for the-internet.
     */
    public static User validUser() {
        return new UserBuilder().build();
    }

    /**
     * A user with a correct username but wrong password.
     * Useful for testing failed login scenarios.
     */
    public static User invalidPasswordUser() {
        return new UserBuilder()
                .withPassword("wrongPassword123")
                .withDisplayName("Tom Smith (bad pass)")
                .build();
    }

    /**
     * A user with a non-existent username.
     */
    public static User unknownUser() {
        return new UserBuilder()
                .withUsername("unknown_user")
                .withPassword("doesNotMatter")
                .withDisplayName("Unknown User")
                .build();
    }
}
