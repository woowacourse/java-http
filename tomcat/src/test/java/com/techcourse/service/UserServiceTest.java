package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @DisplayName("로그인에 성공하면 User 객체를 반환한다.")
    @Test
    void successLoginTest() {
        UserService userService = UserService.getInstance();

        Optional<User> user = userService.login("gugu", "password");

        assertThat(user).isPresent();
    }

    @DisplayName("로그인에 실패하면 Empty를 반환한다.")
    @Test
    void failureLoginTest() {
        UserService userService = UserService.getInstance();

        Optional<User> user = userService.login("jazz", "password");

        assertThat(user).isEmpty();
    }
}
