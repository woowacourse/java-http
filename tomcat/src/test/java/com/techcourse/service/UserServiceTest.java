package com.techcourse.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.techcourse.model.User;
import com.techcourse.prefix.UserPrefix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private final UserService userService;

    public UserServiceTest() {
        this.userService = new UserService();
    }

    @Test
    @DisplayName("존재하지 않는 회원인 경우 에러를 발생한다.")
    void findUser_WhenAccountIsNotCorrespond() {
        User user = UserPrefix.NEW_USER;

        assertThrows(IllegalArgumentException.class,
                () -> userService.findUser(user.getAccount(), user.getPassword()));
    }
}
