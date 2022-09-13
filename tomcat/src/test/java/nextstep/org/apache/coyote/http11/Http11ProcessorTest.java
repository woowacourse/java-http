package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.HttpMessageUtils;
import support.StubSocket;

class Http11ProcessorTest {

    private final RequestMapping requestMapping = RequestMapping.of(
            List.of(new HomeController(), new ResourceController()));

    @DisplayName("GET /index 요청은 index.html 파일을 응답한다")
    @Test
    void index() {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping, new SessionManager());

        processor.process(socket);

        final var actual = socket.output();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 5564 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("index.html"))
        );
    }

    @DisplayName("모든 클라이언트는 서버로부터 세션을 할당받으며 세션 ID 값은 요청 및 응답 메시지의 쿠키에 담긴다.")
    @Nested
    class SessionCookieTest {

        @DisplayName("요청 메시지의 Cookie 헤더에 JSESSIONID 값이 없는 경우, 새로운 세션을 생성하고 응답 메시지의 Set-Cookie 헤더에 해당 세션 ID 값을 할당한다")
        @Test
        void setCookieOnInitialResponse() {
            final String httpRequest = getIndexRequest();
            final var socket = new StubSocket(httpRequest);
            final var sessionManager = new SessionManager();
            final Http11Processor processor = new Http11Processor(socket, requestMapping, sessionManager);

            processor.process(socket);

            assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
        }

        @DisplayName("요청 메시지의 Cookie 헤더에 유효한 JSESSIONID 값이 있는 경우, 대응되는 세션을 그대로 요청에 할당한다.")
        @Test
        void cookieOnRequest() {
            final String initialHttpRequest = getIndexRequest();
            var socket = new StubSocket(initialHttpRequest);
            final var sessionManager = new SessionManager();
            var processor = new Http11Processor(socket, requestMapping, sessionManager);
            processor.process(socket);
            final var sessionId = HttpMessageUtils.extractSetCookieSessionId(socket.output());
            System.out.println(sessionId);

            final var secondHttpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Cookie: JSESSIONID=" + sessionId + " ",
                    "",
                    "");
            socket = new StubSocket(secondHttpRequest);
            processor = new Http11Processor(socket, requestMapping, sessionManager);
            processor.process(socket);

            assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
        }
    }

    private String getIndexRequest() {
        return String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }
}
