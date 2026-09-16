package com.techcourse.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void toStringIncludesPassword() {
        final var user = new User(1L, "gugu", "password", "gugu@example.com");

        assertThat(user.toString()).contains("password='password'");
    }
}
