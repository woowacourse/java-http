package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LoginService 테스트")
class LoginServiceTest {

    private static final String ACCOUNT = "gugu";
    private static final String PASSWORD = "password";

    private final InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();
    private final LoginService loginService = new LoginService(inMemoryUserRepository);

    @DisplayName("로그인 테스트 - 성공")
    @Test
    void login() {
        //given
        //when
        //then
        assertThatCode(() -> loginService.login(ACCOUNT, PASSWORD))
                .doesNotThrowAnyException();
    }

    @DisplayName("로그인 테스트 - 실패 - 존재하지 않는 account")
    @Test
    void loginFailureWhenAccountNotExists() {
        //given
        //when
        //then
        assertThatThrownBy(() -> loginService.login(ACCOUNT + "a", PASSWORD))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @DisplayName("로그인 테스트 - 실패 - 일치하지 않는 password")
    @Test
    void loginFailureWhenPasswordInvalid() {
        //given
        //when
        //then
        assertThatThrownBy(() -> loginService.login(ACCOUNT, PASSWORD + "a"))
                .isInstanceOf(UnAuthorizedException.class);
    }
}