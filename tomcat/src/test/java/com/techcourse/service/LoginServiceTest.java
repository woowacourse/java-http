package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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

            Optional<User> actual = loginService.login("kyum", "password");

            assertThat(actual).hasValue(expected);
        }

        @Test
        @DisplayName("성공 : 유저가 있는 경우 user 반환")
        void loginReturnOptional() {
            LoginService loginService = new LoginService();

            Optional<User> actual = loginService.login("kyum2", "password2");

            assertThat(actual).isEmpty();
        }
    }
}
