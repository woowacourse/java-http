package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    @Test
    @DisplayName("로그인을 한다.")
    void login() {
        LoginService loginService = new LoginService();

        UserResponse userResponse = loginService.login("gugu", "password");

        assertThat(userResponse.email()).isEqualTo("hkkang@woowahan.com");
    }

    @Test
    @DisplayName("계정을 찾을 수 없으면 예외를 던진다.")
    void loginWithInvalidAccount() {
        LoginService loginService = new LoginService();

        assertThatThrownBy(() -> loginService.login("invalid", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외를 던진다.")
    void loginWithInvalidPassword() {
        LoginService loginService = new LoginService();

        assertThatThrownBy(() -> loginService.login("gugu", "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
}
