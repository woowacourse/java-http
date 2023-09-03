package nextstep.jwp.service;

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
}
