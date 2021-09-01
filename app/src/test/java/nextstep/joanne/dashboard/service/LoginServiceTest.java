package nextstep.joanne.dashboard.service;

import nextstep.joanne.dashboard.exception.LoginFailedException;
import nextstep.joanne.dashboard.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginServiceTest {

    @DisplayName("로그인을 한다.")
    @Test
    void login() {
        // given
        String account = "gugu";
        String password = "password";

        // when
        LoginService loginService = new LoginService();
        assertThatCode(() -> loginService.login(account, password))
                .doesNotThrowAnyException();
    }

    @DisplayName("로그인을 한다. - 실패, 존재하지 않는 회원")
    @Test
    void loginFailedWhenUserNotFound() {
        // given
        String account = "mungto";
        String password = "password";

        // when
        LoginService loginService = new LoginService();
        assertThatThrownBy(() -> loginService.login(account, password))
                .isInstanceOf(UserNotFoundException.class);
    }

    @DisplayName("로그인을 한다. - 실패, 패스워드 틀림")
    @Test
    void loginFailed() {
        // given
        String account = "gugu";
        String password = "melong";

        // when
        LoginService loginService = new LoginService();
        assertThatThrownBy(() -> loginService.login(account, password))
                .isInstanceOf(LoginFailedException.class);

    }
}