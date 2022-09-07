package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterServletTest {

    private final RegisterServlet registerServlet = new RegisterServlet();

    private HttpRequest getHttpFormDataRequest(final String rawRequestLine, final String requestBody) {
        final HttpRequestLine requestLine = HttpRequestLine.of(rawRequestLine);
        final Queue<String> rawRequestHeader = new LinkedList<>();
        rawRequestHeader.add("name: eve");
        rawRequestHeader.add("Content-Type: application/x-www-form-urlencoded");
        final HttpHeader httpHeader = HttpHeader.of(rawRequestHeader);

        return HttpRequest.of(requestLine, httpHeader, requestBody);
    }

    @Nested
    @DisplayName("doPost 메소드는")
    class DoPost {

        @Test
        @DisplayName("회원가입에 성공하면 302 상태 코드와 /index.html을 Location 헤더에 담아 반환한다.")
        void success() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /register HTTP/1.1",
                    "account=gugu&password=password&email=abc@email.com");
            final HttpHeader httpHeader = new HttpHeader(new HashMap<>());

            // when
            final ServletResponseEntity response = registerServlet.doPost(httpRequest, httpHeader);

            // then
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo("/index.html");
        }

        @Test
        @DisplayName("account 또는 password 또는 email 쿼리 파라미터가 존재하지 않으면 예외가 발생한다.")
        void exception_noParameter() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("POST /register HTTP/1.1", "account=gugu");
            final HttpHeader httpHeader = new HttpHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> registerServlet.doPost(httpRequest, httpHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No Parameters");
        }
    }
}