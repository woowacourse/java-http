package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private final UserService userService = new UserService();


    @DisplayName("존재하지 않는 account로 로그인 시도시 Optional.empty 반환")
    @Test
    void loginWithInvalidAccount() {
        Map<String, String> body = Map.of(
                "account", "invalidAccount",
                "password", "password"
        );

        Optional<User> user = userService.login(body);

        assertThat(user).isEmpty();
    }

    @DisplayName("잘못된 password로 로그인 시도시 Optional.empty 반환")
    @Test
    void loginWithInvalidPassword() {
        Map<String, String> body = Map.of(
                "account", "gugu",
                "password", "invalidPassword"
        );

        Optional<User> user = userService.login(body);

        assertThat(user).isEmpty();
    }

    @DisplayName("성공 로그인 시도시 Optional.user 반환")
    @Test
    void loginSuccess() {
        Map<String, String> body = Map.of(
                "account", "gugu",
                "password", "password"
        );

        Optional<User> user = userService.login(body);

        assertAll(
                () -> assertThat(user).isPresent(),
                () -> assertThat(user.get().getAccount()).isEqualTo("gugu")
        );
    }

    // 존재하지 않는 account로 조회시 optional.empty() 반환
    // password가 다르면 optional.empty() 반환
    // 제대로 하면 optional.user 반환
    // 존재하는 account로 회원가입시 false 반환
    // 제대로 하면 true 반환

    @DisplayName("존재하는 account로 회원가입시 false 반환")
    @Test
    void registerWithExistedAccount() {
        Map<String, String> body = Map.of(
                "account", "gugu",
                "email", "email@email.com",
                "password", "password2"
        );

        boolean isRegistered = userService.register(body);

        assertThat(isRegistered).isFalse();
    }

    @DisplayName("회원가입 성공 시 true 반환")
    @Test
    void registerSuccess() {
        Map<String, String> body = Map.of(
                "account", "gugu2",
                "email", "email@email.com",
                "password", "password2"
        );

        boolean isRegistered = userService.register(body);

        assertThat(isRegistered).isTrue();
    }
}
