package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.LoginFailureException;
import nextstep.jwp.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginServiceTest {


    @DisplayName("계정이 일치하지 않으면 에러가 발생한다.")
    @Test
    void validateAccount_계정_잘못됨() {
        String account = "가짜gugu";
        String password = "password";
        final LoginService loginService = new LoginService();

        assertThatThrownBy(() -> loginService.validateAccount(account, password))
                .isInstanceOf(LoginFailureException.class);
    }

    @DisplayName("비밀번호가 일치하지 않으면 에러가 발생한다.")
    @Test
    void validateAccount_비밀번호_잘못됨() {
        String account = "gugu";
        String password = "가짜password";
        final LoginService loginService = new LoginService();

        assertThatThrownBy(() -> loginService.validateAccount(account, password))
                .isInstanceOf(LoginFailureException.class);
    }
}
