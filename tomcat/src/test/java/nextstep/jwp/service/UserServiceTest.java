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

    @DisplayName("계정과 비밀번호, 이메일을 입력해 회원가입한다.")
    @Test
    void register() {
        assertThatNoException()
                .isThrownBy(() -> UserService.register("newAccount", "password", "forky@foo.com"));
    }

    @DisplayName("중복된 아이디로 회원가입하면 예외가 발생한다.")
    @Test
    void register_ifAccountNotUnique() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> UserService.register("gugu", "password", "email@foo.com"))
                .withMessage("이미 존재하는 아이디입니다.");
    }

    @DisplayName("잘못된 형식의 이메일로 회원가입하면 예외가 발생한다.")
    @Test
    void register_ifIllegalEmail() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> UserService.register("ggyu", "password", "email.foo.com"))
                .withMessage("이메일 형식이 올바르지 않습니다.");
    }
}
