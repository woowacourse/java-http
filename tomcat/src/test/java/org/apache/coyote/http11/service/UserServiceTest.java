package org.apache.coyote.http11.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @DisplayName("유저를 저장할 수 있다")
    @Test
    void save() {
        User user = new User("testAccount", "password", "email");
        UserService userService = UserService.getInstance();

        User savedUser = userService.save(user);

        assertThat(savedUser).usingRecursiveComparison();
    }
}
