package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("사용자가 유효한 정보를 가지고 있는지 판단할 수 있다.")
    @Test
    void isValid() {
        assertAll(
                () -> assertThat(new User(null, null, null).isValid()).isFalse(),
                () -> assertThat(new User(null, "password", null).isValid()).isFalse(),
                () -> assertThat(new User("hotea", "password", "dbswn990@gmail.com").isValid()).isTrue()
        );
    }
}
