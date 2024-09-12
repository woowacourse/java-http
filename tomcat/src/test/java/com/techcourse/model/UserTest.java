package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("User 객체를 생성한다")
    @Test
    void create() {
        User user = new User(1L, "account", "password", "email");
        assertAll(
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("account", user.getAccount()),
                () -> assertEquals("password", user.getPassword()),
                () -> assertEquals("email", user.getEmail())
        );
    }

    @DisplayName("User 객체를 생성할 때 id를 제외한 값이 널이거나 빈 문자열일 경우 예외를 던진다")
    @Test
    void createWithNull() {
        assertThatThrownBy(() -> new User(1L, null, "password", "email"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User(1L, "account", null, "email"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User(1L, "account", "password", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
