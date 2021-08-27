package nextstep.jwp.service;

import nextstep.jwp.controller.dto.request.LoginRequest;
import nextstep.jwp.exception.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LoginService 테스트")
class LoginServiceTest {

    private static final String ACCOUNT = "gugu";
    private static final String PASSWORD = "password";

    private final LoginService loginService = new LoginService();

    @DisplayName("로그인 테스트 - 성공")
    @Test
    void login() {
        //given
        final LoginRequest loginRequest = new LoginRequest(ACCOUNT, PASSWORD);

        //when
        //then
        assertThatCode(() -> loginService.login(loginRequest))
                .doesNotThrowAnyException();
    }

    @DisplayName("로그인 테스트 - 실패 - 존재하지 않는 account")
    @Test
    void loginFailureWhenAccountNotExists() {
        //given
        final LoginRequest loginRequest = new LoginRequest(ACCOUNT + "a", PASSWORD);

        //when
        //then
        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @DisplayName("로그인 테스트 - 실패 - 일치하지 않는 password")
    @Test
    void loginFailureWhenPasswordInvalid() {
        //given
        final LoginRequest loginRequest = new LoginRequest(ACCOUNT, PASSWORD + "a");

        //when
        //then
        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(UnAuthorizedException.class);
    }
}