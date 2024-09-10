package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.LoginRequestDto;
import com.techcourse.dto.RegisterRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LoginServiceTest {

    private final LoginService loginService = new LoginService();

    @Test
    @DisplayName("회원가입 시 정보를 저장한다.")
    void register() {
        loginService.register(new RegisterRequestDto("test", "test@email.com", "password"));
        assertThat(InMemoryUserRepository.findByAccount("test")).isNotEmpty();
    }

    @Test
    @DisplayName("회원가입된 정보로 로그인한다.")
    void login() {
        assertThatCode(() -> loginService.login(new LoginRequestDto("gugu", "password")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("로그인 시 일치하는 정보가 없으면 예외를 발생한다.")
    void validateLogin() {
        assertThatThrownBy(() -> loginService.login(new LoginRequestDto("gugu2", "password")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> loginService.login(new LoginRequestDto("gugu", "wrong")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
