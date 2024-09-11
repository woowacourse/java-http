package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserAccountTest {

    @DisplayName("유저 계정이 비어있으면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "      "})
    void validateUserAccount(String account) {
        //when //then
        assertThatThrownBy(() -> new UserAccount(account))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저명은 공백일 수 없습니다.");
    }
}
