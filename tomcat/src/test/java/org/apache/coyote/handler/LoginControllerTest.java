package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestContentType;
import org.apache.coyote.request.RequestUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("유저 로그인을 할 수 있다.") 
    void login_true() {
        LoginController loginController = new LoginController();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password", "password");
        Request loginRequest = new Request(RequestUrl.of("login", queryString), List.of(RequestContentType.ALL));

        boolean isLogin = loginController.login(loginRequest);
        assertThat(isLogin).isTrue();
    }

    @Test
    @DisplayName("회원 정보가 없는 경우 유저 로그인을 할 수 없다.")
    void login_false() {
        LoginController loginController = new LoginController();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password", "password123");
        Request loginRequest = new Request(RequestUrl.of("login", queryString), List.of(RequestContentType.ALL));

        boolean isLogin = loginController.login(loginRequest);
        assertThat(isLogin).isFalse();
    }

    @Test
    @DisplayName("account 키를 잘못 전달한 경우 예외가 발생한다..")
    void login_key_exception1() {
        LoginController loginController = new LoginController();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account1", "gugu");
        queryString.put("password", "password");
        Request loginRequest = new Request(RequestUrl.of("login", queryString), List.of(RequestContentType.ALL));

        assertThatThrownBy(() -> loginController.login(loginRequest))
                .isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("password 키를 잘못 전달한 경우 예외가 발생한다..")
    void login_key_exception2() {
        LoginController loginController = new LoginController();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password1", "password");
        Request loginRequest = new Request(RequestUrl.of("login", queryString), List.of(RequestContentType.ALL));

        assertThatThrownBy(() -> loginController.login(loginRequest))
                .isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("키가 없는 경우 예외가 발생한다..")
    void login_key_exception3() {
        LoginController loginController = new LoginController();

        Map<String, String> queryString = new HashMap<>();
        Request loginRequest = new Request(RequestUrl.of("login", queryString), List.of(RequestContentType.ALL));

        assertThatThrownBy(() -> loginController.login(loginRequest))
                .isInstanceOf(LoginException.class);
    }
}
