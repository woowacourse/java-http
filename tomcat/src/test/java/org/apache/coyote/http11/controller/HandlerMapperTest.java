package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

    @DisplayName("requestUrl이 login을 가지면 loginController를 반환한다")
    @Test
    void mapToLoginController() {
        String loginRequestUri = "/login.html";

        assertThat(HandlerMapper.mapTo(loginRequestUri)).isInstanceOf(LoginController.class);
    }

    @DisplayName("requestUrl이 register을 가지면 UserController를 반환한다")
    @Test
    void mapToUserController() {
        String loginRequestUri = "/register";

        assertThat(HandlerMapper.mapTo(loginRequestUri)).isInstanceOf(UserController.class);
    }

    @DisplayName("핸들러의 존재 여부를 알 수 있다")
    @Test
    void hasHandler() {
        String existsUrl = "/login";
        String noneExistsUrl = "";

        assertAll(
                () -> assertThat(HandlerMapper.hasHandler(existsUrl)).isTrue(),
                () -> assertThat(HandlerMapper.hasHandler(noneExistsUrl)).isFalse()
        );
    }
}
