package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.HttpSession;
import nextstep.jwp.request.HttpSessions;
import nextstep.jwp.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ControllerTest {
    private final Controller controller;

    ControllerTest() {
        this.controller = new Controller();
    }

    @DisplayName("GET 로그인 할 때,")
    @Nested
    class GetLogin {
        @DisplayName("쿠키에 유효한 sessionId가 있다면, index 페이지로 리다이렉트 한다.")
        @Test
        void containsCookie() throws IOException {
            // given
            final HttpSession httpSession = new HttpSession("1234");
            User user = new User(Long.MAX_VALUE, "sample", "password", "sample@email.com");
            httpSession.setAttribute("user", user);
            HttpSessions.addSession(httpSession);
            final String request = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Cookie: JSESSIONID=1234 ",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            HttpRequest httpRequest = new HttpRequest(new BufferedReader(bufferedReader));

            // when
            String actual = controller.login(httpRequest);

            // then
            assertThat(actual).isEqualTo(HttpResponse.redirectTo("/index.html"));
            HttpSessions.removeSession(httpSession);
        }

        @DisplayName("쿠키에 유효하지 않은 sessionId가 있다면, 인증되지 않은 사용자 예외를 내보낸다.")
        @Test
        void containsCookieInvalidSession() throws IOException {
            // given
            final String request = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Cookie: JSESSIONID=1234 ",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            HttpRequest httpRequest = new HttpRequest(new BufferedReader(bufferedReader));

            // when
            // then
            assertThatThrownBy(() -> controller.login(httpRequest)).isInstanceOf(UnauthorizedException.class);
        }

        @DisplayName("쿠키가 없다면, login 페이지를 보여준다.")
        @Test
        void notContainsCookie() throws IOException {
            // given
            final String request = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            HttpRequest httpRequest = new HttpRequest(new BufferedReader(bufferedReader));

            // when
            String actual = controller.login(httpRequest);
            String expected = HttpResponse
                    .responseResource("/login.html")
                    .build();

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }
}