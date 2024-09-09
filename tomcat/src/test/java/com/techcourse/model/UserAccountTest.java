package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserAccountTest {

    @DisplayName("유저 계정이 비어있으면 예외가 발생한다.")
    @Test
    void validateUserAccount() {
        //given
        String account = "";

        //when //then
        assertThatThrownBy(() -> new UserAccount(account))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저명은 공백일 수 없습니다.");
    }
}
