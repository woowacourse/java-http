package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class LoginServiceTest {


    @Nested
    @DisplayName("로그인 확인")
    class login {

        @Test
        @DisplayName("성공 : 유저가 있는 경우 user 반환")
        void loginReturnUser() {
            User expected = new User("kyum", "password", "kyum@naver.com");
            InMemoryUserRepository.save(expected);
            LoginService loginService = new LoginService();

            User actual = loginService.login("kyum", "password");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 : 유저가 있는 경우 예외 발생")
        void loginReturnOptional() {
            LoginService loginService = new LoginService();

            assertThatThrownBy(() -> loginService.login("kyum2", "password2"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("로그인 정보가 잘못되었습니다.");

        }
    }
}
