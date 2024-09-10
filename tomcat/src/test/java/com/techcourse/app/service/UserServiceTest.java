package com.techcourse.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.app.exception.AuthenticationException;
import com.techcourse.app.exception.DuplicatedException;
import com.techcourse.app.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    @DisplayName("로그인을 한다.")
    void login() {
        UserService userService = new UserService();

        User user = userService.login("gugu", "password");

        assertThat(user.getEmail()).isEqualTo("hkkang@woowahan.com");
    }

    @Test
    @DisplayName("계정을 찾을 수 없으면 예외를 던진다.")
    void loginWithInvalidAccount() {
        UserService userService = new UserService();

        assertThatThrownBy(() -> userService.login("invalid", "password"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외를 던진다.")
    void loginWithInvalidPassword() {
        UserService userService = new UserService();

        assertThatThrownBy(() -> userService.login("gugu", "invalid"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("회원가입을 한다.")
    void register() {
        UserService userService = new UserService();

        User user = userService.register("loki", "loki@email.com", "password");

        assertThat(user.getAccount()).isEqualTo("loki");
    }

    @Test
    @DisplayName("이미 존재하는 계정으로 회원가입하려고 하면 예외를 던진다.")
    void registerWithDuplicatedAccount() {
        UserService userService = new UserService();

        assertThatThrownBy(() -> userService.register("gugu", "gugu@email.com", "password"))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("이미 존재하는 계정입니다.");
    }
}
