package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private final LoginServlet loginHandler = new LoginServlet();

    private HttpRequest getHttpFormDataRequest(final String rawRequestLine, final String requestBody) {
        final HttpRequestLine requestLine = HttpRequestLine.of(rawRequestLine);
        final List<String> rawRequestHeader = new ArrayList<>();
        rawRequestHeader.add("name: eve");
        rawRequestHeader.add("Content-Type: application/x-www-form-urlencoded");
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.of(rawRequestHeader);

        return HttpRequest.of(requestLine, httpRequestHeader, requestBody);
    }

    @Nested
    @DisplayName("doPost 메소드는")
    class DoPost {

        @Test
        @DisplayName("로그인에 성공하면 302 상태 코드와 /index.html을 Location 헤더에 담아 반환한다.")
        void success() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /login HTTP/1.1", "account=gugu&password=password");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when
            final ServletResponseEntity response = loginHandler.doPost(httpRequest, httpResponseHeader);

            // then
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo("/index.html");
        }

        @Test
        @DisplayName("account 또는 password 쿼리 파라미터가 존재하지 않으면 예외가 발생한다.")
        void exception_noParameter() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /login HTTP/1.1", "account=gugu");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginHandler.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No Parameters");
        }

        @Test
        @DisplayName("입력한 정보와 일치하는 회원이 없다면 예외가 발생한다.")
        void exception_userNotFound() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /login HTTP/1.1", "account=eve&password=password");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginHandler.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("올바르지 않은 비밀번호를 입력하면 예외가 발생한다.")
        void exception_invalidPassword() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /login HTTP/1.1", "account=gugu&password=wrong");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginHandler.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("User not found");
        }
    }
}