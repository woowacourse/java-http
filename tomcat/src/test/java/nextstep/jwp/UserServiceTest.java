package nextstep.jwp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @DisplayName("로그인 성공 시 아무 예외도 발생하지 않음")
    @Test
    void loginSuccess() {
        final var userService = new UserService();

        final var id = "gugu";
        final var password = "password";

        assertDoesNotThrow(() -> userService.login(Map.of("account", id, "password", password)));
    }

    @DisplayName("존재하지 않는 아이디로 조회 시 예외 발생")
    @Test
    void loginFail_idDoesNotExist_throwsException() {
        final var userService = new UserService();

        final var unavailableId = "brown";
        final var password = "password";

        assertThrowsExactly(
                NoSuchElementException.class,
                () -> userService.login(Map.of("account", unavailableId, "password", password))
        );
    }

    @DisplayName("비밀번호가 틀렸을 시 예외 발생")
    @Test
    void loginFail_passwordNotMatch_throwsException() {
        final var userService = new UserService();

        final var id = "gugu";
        final var wrongPassword = "wrongpassword";

        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> userService.login(Map.of("account", id, "password", wrongPassword))
        );
    }
}
