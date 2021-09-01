package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.exception.UserNotFoundException;
import nextstep.jwp.service.exception.UserPasswordInValidException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.security.InvalidParameterException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserServiceTest {

    private UserService userService;

    @BeforeEach
    private void setUp() {
        userService = new UserService();
    }

    @Test
    void login() {
        User user = userService.login("gugu", "password");

        Assertions.assertThat(user.getAccount()).isEqualTo("gugu");
        Assertions.assertThat(user.getPassword()).isEqualTo("password");

    }

    @Test
    void loginFailInvalidPassword() {
        assertThatThrownBy(() -> {
            userService.login("gugu", "test");
        }).isInstanceOf(UserPasswordInValidException.class);
    }


    @Test
    void loginFailUserNotFound() {
        assertThatThrownBy(() -> {
            userService.login("test", "password");
        }).isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void save() {
        userService.save("test", "password", "test@test.com");

        User user = InMemoryUserRepository.findByAccount("test").get();
        Assertions.assertThat(user.getAccount()).isEqualTo("test");
        Assertions.assertThat(user.getPassword()).isEqualTo("password");
        Assertions.assertThat(user.getEmail()).isEqualTo("test@test.com");
    }
}