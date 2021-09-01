package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.exception.UserNotFoundException;
import nextstep.jwp.service.exception.UserPasswordInValidException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserServiceTest {

    private UserService userService;

    @BeforeEach
    private void setUp() {
        userService = new UserService();
    }

    @DisplayName("로그인이 성공한다.")
    @Test
    void login() {
        User user = userService.login("gugu", "password");

        Assertions.assertThat(user.getAccount()).isEqualTo("gugu");
        Assertions.assertThat(user.getPassword()).isEqualTo("password");
    }

    @DisplayName("비밀번호 미일치 시 로그인 실패 한다.")
    @Test
    void loginFailInvalidPassword() {
        assertThatThrownBy(() -> {
            userService.login("gugu", "test");
        }).isInstanceOf(UserPasswordInValidException.class);
    }

    @DisplayName("입력된 아이디로 유저를 찾을 수 없는 경우 로그인 실패 한다.")
    @Test
    void loginFailUserNotFound() {
        assertThatThrownBy(() -> {
            userService.login("test12345", "password");
        }).isInstanceOf(UserNotFoundException.class);
    }

    @DisplayName("회원가입이 성공한다.")
    @Test
    void save() {
        userService.save("test", "password", "test@test.com");

        User user = InMemoryUserRepository.findByAccount("test").get();
        Assertions.assertThat(user.getAccount()).isEqualTo("test");
        Assertions.assertThat(user.getPassword()).isEqualTo("password");
        Assertions.assertThat(user.getEmail()).isEqualTo("test@test.com");
    }
}