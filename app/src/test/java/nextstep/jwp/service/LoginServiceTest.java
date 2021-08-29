package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private LoginService loginService;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        Map<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, 1L);

        loginService = new LoginService(userRepository);
    }

    @DisplayName("login 시도시")
    @Nested
    class login {

        @DisplayName("일치하는 account가 없다면 예외가 발생한다.")
        @Test
        void notFoundAccount() {
            // when, then
            assertThatThrownBy(() -> loginService.login("account", "password"))
                .isExactlyInstanceOf(UnauthorizedException.class);
        }

        @DisplayName("password가 일치하지 않는다면 예외가 발생한다.")
        @Test
        void unmatchedPassword() {
            // given
            String account = "account";
            String password = "password";
            userRepository.save(new User(account, password, "email"));

            // when, then
            assertThatThrownBy(() -> loginService.login(account, password + "something"))
                .isExactlyInstanceOf(UnauthorizedException.class);
        }
    }
}
