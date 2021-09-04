package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.controller.response.LoginResponse;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.model.User;
import nextstep.jwp.server.HttpSessions;
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
        userRepository = new InMemoryUserRepository(database, new AtomicLong(1));

        HttpSessions httpSessions = new HttpSessions();
        loginService = new LoginService(userRepository, httpSessions);
    }

    @DisplayName("login 시도시")
    @Nested
    class login {

        @DisplayName("성공하면 LoginResponse를 반환한다.")
        @Test
        void success() {
            // given
            String account = "account";
            String password = "password";
            userRepository.save(new User(account, password, "email"));

            LoginRequest loginRequest = new LoginRequest(account, password);

            // when, then
            assertThat(loginService.login(loginRequest)).isExactlyInstanceOf(LoginResponse.class);
        }

        @DisplayName("password가 일치하지 않는다면 예외가 발생한다.")
        @Test
        void unmatchedPassword() {
            // given
            String account = "account";
            String password = "password";
            userRepository.save(new User(account, password, "email"));

            LoginRequest loginRequest = new LoginRequest(account, password + "123");

            // when, then
            assertThatThrownBy(() -> loginService.login(loginRequest))
                .isExactlyInstanceOf(UnauthorizedException.class);
        }

        @DisplayName("일치하는 account가 없다면 예외가 발생한다.")
        @Test
        void notFoundAccount() {
            // given
            LoginRequest loginRequest = new LoginRequest("account", "password");

            // when, then
            assertThatThrownBy(() -> loginService.login(loginRequest))
                .isExactlyInstanceOf(UnauthorizedException.class);
        }
    }

    @DisplayName("이미 로그인 중인지 확인했을 때")
    @Nested
    class IsAlreadyLogin {

        private static final String account = "account";
        private static final String password = "password";

        @DisplayName("로그인 중이었다면 true를 반환한다.")
        @Test
        void isTrue() {
            // given
            userRepository.save(new User(account, password, "email"));
            LoginResponse loginResponse = loginService.login(new LoginRequest(account, password));

            HttpCookie httpCookie = HttpCookie.parse(loginResponse.toCookieString());

            // when
            boolean alreadyLogin = loginService.isAlreadyLogin(httpCookie);

            // then
            assertThat(alreadyLogin).isTrue();
        }

        @DisplayName("로그인 중이 아니라면 false를 반환한다.")
        @Test
        void isFalse() {
            // given
            HttpCookie httpCookie = HttpCookie.parse("JSESSIONID=1234;");

            // when
            boolean alreadyLogin = loginService.isAlreadyLogin(httpCookie);

            // then
            assertThat(alreadyLogin).isFalse();
        }
    }
}
