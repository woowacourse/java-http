package com.techcourse.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void toStringDoesNotExposePassword() {
        final var user = new User(1L, "gugu", "super-secret", "gugu@example.com");

        assertThat(user.toString())
                .contains("gugu")
                .doesNotContain("super-secret");
    }
}
