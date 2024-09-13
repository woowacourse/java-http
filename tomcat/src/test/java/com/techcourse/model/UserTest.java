package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("사용자가 유효한 정보를 가지고 있는지 판단할 수 있다.")
    @Test
    void isValid() {
        assertAll(
                () -> assertThatThrownBy(() -> new User(null, null, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 필수 정보가 누락되었습니다."),
                () -> assertThatThrownBy(() -> new User(null, "", null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 필수 정보가 누락되었습니다."),
                () -> assertThatThrownBy(() -> new User(null, null, "  "))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 필수 정보가 누락되었습니다."),
                () -> assertThatThrownBy(() -> new User(" ", null, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 필수 정보가 누락되었습니다."),
                () -> assertThatThrownBy(() -> new User("", "", ""))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 필수 정보가 누락되었습니다.")
        );
    }
}
