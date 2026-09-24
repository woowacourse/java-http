package org.apache.coyote.http11;

import com.techcourse.model.User;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}\\r\\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: 12\r\n")
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}\\r\\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: 5564\r\n")
                .endsWith("\r\n\r\n" + new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    @Test
    void 최초_요청에서_발급된_세션에_로그인한_사용자를_저장한다() {
        // given
        final var firstRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var firstSocket = new StubSocket(firstRequest);
        new Http11Processor(firstSocket).process(firstSocket);

        final var sessionId = extractSessionId(firstSocket);
        final var sessionBeforeLogin = SessionManager.getInstance().findSession(sessionId);

        final var requestBody = "account=gugu&password=password";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "content-length: " + requestBody.length(),
                "Cookie: JSESSIONID=" + sessionId,
                "",
                requestBody);

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var sessionAfterLogin = SessionManager.getInstance().findSession(sessionId);
        final var user = (User) sessionAfterLogin.getAttribute("user");

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n")
                .doesNotContain("Set-Cookie");
        assertThat(sessionAfterLogin).isSameAs(sessionBeforeLogin);
        assertThat(user.getAccount()).isEqualTo("gugu");
    }

    @Test
    void 로그인에_실패하면_인증_실패_페이지로_리다이렉트한다() {
        // given
        final var requestBody = "account=gugu&password=wrong";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}\\r\\n");
    }

    @Test
    void 소문자_cookie_헤더로도_로그인_상태를_확인한다() {
        // given
        final var requestBody = "account=gugu&password=password";
        final var loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        final var loginSocket = new StubSocket(loginRequest);
        new Http11Processor(loginSocket).process(loginSocket);

        final var sessionId = loginSocket.output()
                .lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();

        final var request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "cookie: JSESSIONID=" + sessionId,
                "",
                "");
        final var socket = new StubSocket(request);

        // when
        new Http11Processor(socket).process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "\r\n");

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String extractSessionId(final StubSocket socket) {
        return socket.output()
                .lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();
    }
}
