package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("유저 로그인을 할 수 있다.")
    void login_query_string_true() {
        LoginHandler loginHandler = new LoginHandler();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password", "password");

        boolean isLogin = loginHandler.login(queryString.get("account"), queryString.get("password"));
        assertThat(isLogin).isTrue();
    }

    @Test
    @DisplayName("회원 정보가 잘못된 경우 유저 로그인을 할 수 없다.")
    void login_query_string_false() {
        LoginHandler loginHandler = new LoginHandler();

        Map<String, String> queryString = new HashMap<>();
        queryString.put("account", "gugu");
        queryString.put("password", "password123");

        boolean isLogin = loginHandler.login(queryString.get("account"), queryString.get("password"));
        assertThat(isLogin).isFalse();
    }
}
