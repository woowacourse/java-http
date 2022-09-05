package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @DisplayName("계정과 비밀번호를 입력해 로그인한다.")
    @Test
    void login() {
        assertThatNoException()
                .isThrownBy(() -> UserService.login("gugu", "password"));
    }

    @DisplayName("존재하지 않는 계정으로 로그인하면 예외가 발생한다.")
    @Test
    void login_ifUserNotFound() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> UserService.login("ggyu", "password"));
    }

    @DisplayName("잘못된 비밀번호로 로그인하면 예외가 발생한다.")
    @Test
    void login_ifWrongPassword() {
        assertThatExceptionOfType(UnauthorizedException.class)
                .isThrownBy(() -> UserService.login("gugu", "wrong"));
    }
}
