package nextstep.jwp.service;

import nextstep.jwp.exception.DuplicatedAccountException;
import nextstep.jwp.exception.InvalidEmailFormException;
import nextstep.jwp.exception.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceTest {

    private final UserService userService = new UserService();

    @DisplayName("로그인에 성공한다")
    @Test
    void success_login() {
        // given
        String account = "gugu";
        String password = "password";

        // when & then
        assertDoesNotThrow(() -> userService.login(account, password));
    }

    @DisplayName("어카운트가 틀리면 예외를 발생시킨다")
    @Test
    void throws_exception_when_invalid_account() {
        // given
        String account = "gigi";
        String password = "password";

        // when & then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @DisplayName("패스워드가 틀리면 예외를 발생시킨다")
    @Test
    void throws_exception_when_invalid_password() {
        // given
        String account = "gugu";
        String password = "gugu";

        // when & then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @DisplayName("회원가입에 성공한다")
    @Test
    void success_register() {
        // given
        String account = "test1";
        String password = "1234";
        String email = "test@test.com";

        // when & then
        assertDoesNotThrow(() -> userService.register(account, password, email));
    }

    @DisplayName("중복된 아이디가 있다면 회원가입에 실패한다.")
    @Test
    void throws_exception_when_already_being_account() {
        // given
        String account = "test2";
        String password = "1234";
        String email = "test@test.com";

        // when
        userService.register(account, password, email);

        // then
        assertThatThrownBy(() -> userService.register(account, password, email))
                .isInstanceOf(DuplicatedAccountException.class);
    }

    @DisplayName("이메일 형식에 맞지 않으면 예외를 발생한다")
    @Test
    void throws_exception_when_invalid_email_form() {
        // given
        String account = "test3";
        String password = "1234";
        String email = "test.com";

        // when & then
        assertThatThrownBy(() -> userService.register(account, password, email))
                .isInstanceOf(InvalidEmailFormException.class);
    }
}
