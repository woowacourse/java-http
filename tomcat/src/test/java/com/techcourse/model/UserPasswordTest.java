package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPasswordTest {

    @DisplayName("유저 비밀번호이 비어있으면 예외가 발생한다.")
    @Test
    void validateUserPassword() {
        //given
        String account = "";

        //when //then
        assertThatThrownBy(() -> new UserPassword(account))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 공백일 수 없습니다.");
    }
}
