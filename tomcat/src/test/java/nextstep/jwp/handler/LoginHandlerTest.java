package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private final LoginServlet loginHandler = new LoginServlet();

    @Nested
    @DisplayName("doPost 메소드는")
    class DoPost {

        @Test
        @DisplayName("로그인에 성공하면 /login.html을 반환한다.")
        void success() {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=gugu&password=password HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when
            final ServletResponseEntity response = loginHandler.doPost(httpRequest);

            // then
            assertThat(response.getResource()).isEqualTo("/login.html");
        }

        @Test
        @DisplayName("account 또는 password 쿼리 파라미터가 존재하지 않으면 예외가 발생한다.")
        void exception_noParameter() {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=gugu HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when & then
            assertThatThrownBy(() -> loginHandler.doPost(httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No Parameters");
        }

        @Test
        @DisplayName("입력한 정보와 일치하는 회원이 없다면 예외가 발생한다.")
        void exception_userNotFound() {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=gugu&password=wrong HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when & then
            assertThatThrownBy(() -> loginHandler.doPost(httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }
    }
}