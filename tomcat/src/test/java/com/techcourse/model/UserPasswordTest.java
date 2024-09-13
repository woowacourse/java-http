package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserPasswordTest {

    @DisplayName("유저 비밀번호이 비어있으면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "      "})
    void validateUserPassword(String password) {
        //when //then
        assertThatThrownBy(() -> new UserPassword(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 공백일 수 없습니다.");
    }
}
