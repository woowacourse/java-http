package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class RegisterServiceTest {

    @Nested
    @DisplayName("회원가입")
    class registerUser {

        @Test
        @DisplayName("성공 : 회원 가입")
        void registerUserSuccess() {
            User expected = new User("pokpo", "password", "kyum@naver.com");
            RegisterService registerService = new RegisterService();

            registerService.registerUser("pokpo", "password", "kyum@naver.com");

            Optional<User> actual = InMemoryUserRepository.findByAccount("pokpo");
            assertThat(actual).hasValue(expected);
        }

        @Test
        @DisplayName("실패 : 이미 유저가 존재해서 실패")
        void registerUserFail() {
            RegisterService registerService = new RegisterService();

            assertThatThrownBy(() ->
                    registerService.registerUser("gugu", "password1", "dd@naver.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 아이디입니다.");
        }
    }
}
