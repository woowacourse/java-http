package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthException;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthServiceTest {

    private SessionManager sessionManager;
    private InMemoryUserRepository inMemoryUserRepository;

    @BeforeEach
    void setUp() {
        inMemoryUserRepository = InMemoryUserRepository.getInstance();
        inMemoryUserRepository.deleteAll();
        sessionManager = SessionManager.getInstance();
        sessionManager.deleteAll();
    }

    @Test
    void 로그인한다() {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        authService.register("gugu", "password", "hkkang@woowahan.com");

        // when
        String sessionId = authService.login("gugu", "password");

        // then
        assertThat(sessionId).isNotEmpty();
    }

    @Test
    void 존재하지_않는_계정으로_로그인을_할_경우_예외를_던진다() {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        String wrongAccount = "wrongAccount";
        String password = "password";

        // expect
        assertThatThrownBy(() -> authService.login(wrongAccount, password))
                .isInstanceOf(AuthException.class)
                .hasMessage("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
    }

    @Test
    void 틀린_비밀번호로_로그인_할_경우_예외를_던진다() {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        authService.register("gugu", "password", "hkkang@woowahan.com");
        String account = "gugu";
        String wrongPassword = "wrongPassword";

        // expect
        assertThatThrownBy(() -> authService.login(account, wrongPassword))
                .isInstanceOf(AuthException.class)
                .hasMessage("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
    }

    @Test
    void 회원가입한다() {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        String account = "account";
        String password = "password";
        String email = "account@email.com";

        // when
        String sessionId = authService.register(account, password, email);

        // then
        assertThat(sessionId).isNotEmpty();
    }

    @Test
    void 회원가입할_때_계정이_중복될_경우_예외를_던진다() {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        String duplicateAccount = "duplicateAccount";
        authService.register(duplicateAccount, "password", "account@email.com");

        // expect
        assertThatThrownBy(() -> authService.register(duplicateAccount, "password", "account@emali.com"))
                .isInstanceOf(AuthException.class)
                .hasMessage("중복된 계정 입니다.");
    }

    @ParameterizedTest
    @CsvSource({"notLoggedInId, false", "loggedInId, true"})
    void session_Id로_이미_로그인이_되었는지_확인한다(String sessionId, boolean expected) {
        // given
        AuthService authService = new AuthService(sessionManager, inMemoryUserRepository);
        Session session = new Session("loggedInId");
        sessionManager.add(session);

        // when
        boolean actual = authService.isLoggedIn(sessionId);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
