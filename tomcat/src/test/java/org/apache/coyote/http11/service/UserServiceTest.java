package org.apache.coyote.http11.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceTest {

    @Test
    void 유저_로그인을_할_수_있다() {
        UserService userService = new UserService();

        assertDoesNotThrow(() -> userService.doRequest(Map.of("account", "gugu", "password", "password")));
    }

    @Test
    void 존재하지_않는_유저일시_예외가_발생한다() {
        UserService userService = new UserService();

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> userService.doRequest(Map.of("account", "tuda", "password", "password")));
    }
}
