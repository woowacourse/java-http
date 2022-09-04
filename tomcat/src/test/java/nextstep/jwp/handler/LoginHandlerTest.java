package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private final LoginHandler loginHandler = new LoginHandler();

    @Nested
    @DisplayName("handle 메소드는")
    class Handle {

        @Test
        @DisplayName("로그인에 성공하면 /login.html을 반환한다.")
        void success() {
            // given
            final Map<String, String> queryParams = new HashMap<>();
            queryParams.put("account", "gugu");
            queryParams.put("password", "password");

            // when
            final String response = loginHandler.handle(queryParams);

            // then
            assertThat(response).isEqualTo("/login.html");
        }

        @Test
        @DisplayName("account 또는 password 쿼리 파라미터가 존재하지 않으면 예외가 발생한다.")
        void exception_noParameter() {
            // given
            final Map<String, String> queryParams = new HashMap<>();

            // when & then
            assertThatThrownBy(() -> loginHandler.handle(queryParams))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No Parameters");
        }

        @Test
        @DisplayName("입력한 정보와 일치하는 회원이 없다면 예외가 발생한다.")
        void exception_userNotFound() {
            // given
            final Map<String, String> queryParams = new HashMap<>();
            queryParams.put("account", "gugu");
            queryParams.put("password", "wrong");

            // when & then
            assertThatThrownBy(() -> loginHandler.handle(queryParams))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }
    }
}