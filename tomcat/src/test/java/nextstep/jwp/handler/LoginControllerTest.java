package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = LoginController.getInstance();

    private HttpRequest getHttpRequest(final String rawRequestLine, final List<String> rawRequestHeader,
                                       final String requestBody) {
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(rawRequestHeader);

        return HttpRequest.of(requestLine, httpRequestHeader, requestBody);
    }

    private HttpRequest getHttpFormDataRequest(final String requestBody) {
        final List<String> rawRequestHeader = new ArrayList<>();
        rawRequestHeader.add("Content-Type: application/x-www-form-urlencoded");

        return getHttpRequest("POST /login HTTP/1.1", rawRequestHeader, requestBody);
    }

    @Test
    @DisplayName("doGet 메소드는 요청 헤더의 쿠키에 세션 ID가 포함되어 있지 않다면 /login.html 응답을 반환한다.")
    void doGet() {
        final HttpRequest httpRequest = getHttpRequest("GET /login HTTP/1.1", new ArrayList<>(), "");

        // when
        final HandlerResponseEntity response = loginController.doGet(httpRequest,
                new HttpResponseHeader(new HashMap<>()));

        // then
        assertThat(response.getResource()).isEqualTo("/login.html");

    }

    @Nested
    @DisplayName("doPost 메소드는")
    class DoPost {

        @Test
        @DisplayName("로그인에 성공하면 302 상태 코드와 /index.html, JSessionId 값을 각각 Location, Set-Cookie 헤더에 담아 반환한다.")
        void success() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("account=gugu&password=password");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when
            final HandlerResponseEntity response = loginController.doPost(httpRequest, httpResponseHeader);

            // then
            Assertions.assertAll(() -> {
                assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
                assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo("/index.html");
                assertThat(response.getHttpHeader().getCookies().getCookie("JSESSIONID")).isNotEmpty();
            });
        }

        @Test
        @DisplayName("account 또는 password 쿼리 파라미터가 존재하지 않으면 예외가 발생한다.")
        void exception_noParameter() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("account=gugu");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginController.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No Parameters");
        }

        @Test
        @DisplayName("입력한 정보와 일치하는 회원이 없다면 예외가 발생한다.")
        void exception_userNotFound() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("account=eve&password=password");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginController.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("올바르지 않은 비밀번호를 입력하면 예외가 발생한다.")
        void exception_invalidPassword() {
            // given
            final HttpRequest httpRequest = getHttpFormDataRequest("account=gugu&password=wrong");
            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(new HashMap<>());

            // when & then
            assertThatThrownBy(() -> loginController.doPost(httpRequest, httpResponseHeader))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("User not found");
        }
    }
}