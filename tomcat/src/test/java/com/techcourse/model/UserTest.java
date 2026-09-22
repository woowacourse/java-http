package com.techcourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("필수 값이 null이면 생성할 수 없다.")
    void createFailWhenRequiredValueIsNull() {
        assertThatThrownBy(() -> new User(null, "password", "email@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User("account", null, "email@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User("account", "password", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("필수 값이 공백이면 생성할 수 없다.")
    void createFailWhenRequiredValueIsBlank() {
        assertThatThrownBy(() -> new User("", "password", "email@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User("account", " ", "email@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new User("account", "password", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
