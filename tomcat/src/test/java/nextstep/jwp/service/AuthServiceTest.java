package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthServiceTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void 로그인한다() {
        // given
        AuthService authService = new AuthService(sessionManager);
        String account = "gugu";
        String password = "password";

        // when
        String sessionId = authService.login(account, password);

        // then
        assertThat(sessionId).isNotEmpty();
    }

    @Test
    void 존재하지_않는_계정으로_로그인을_할_경우_예외를_던진다() {
        // given
        AuthService authService = new AuthService(sessionManager);
        String wrongAccount = "wrongAccount";
        String password = "password";

        // expect
        assertThatThrownBy(() -> authService.login(wrongAccount, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
    }

    @Test
    void 틀린_비밀번호로_로그인_할_경우_예외를_던진다() {
        // given
        AuthService authService = new AuthService(sessionManager);
        String account = "gugu";
        String wrongPassword = "wrongPassword";

        // expect
        assertThatThrownBy(() -> authService.login(account, wrongPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
    }

    @Test
    void 회원가입한다() {
        // given
        AuthService authService = new AuthService(sessionManager);
        String account = "account";
        String password = "password";
        String email = "account@email.com";

        // when
        String sessionId = authService.register(account, password, email);

        // then
        assertThat(sessionId).isNotEmpty();
    }
}
