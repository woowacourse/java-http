package org.apache.coyote.http11;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

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
                .containsPattern("Set-Cookie: JSESSIONID=" + UUID_PATTERN)
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: 12\r\n")
                .endsWith("Hello world!");
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
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=" + UUID_PATTERN)
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: 5564\r\n")
                .endsWith("\r\n" + responseBody);
    }

    @Test
    void 세션_쿠키가_있는_요청에는_세션_쿠키를_추가하지_않는다() {
        // given
        final String sessionId = UUID.randomUUID().toString();
        SessionManager.getInstance().add(new Session(sessionId));
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .doesNotContain("Set-Cookie:")
                .endsWith("Hello world!");
    }

    @Test
    void 로그인_성공_리다이렉트_응답에_세션_쿠키를_추가한다() {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=" + UUID_PATTERN);
    }

    @Test
    void 로그인_성공_시_세션에_사용자를_저장한다() {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String sessionId = extractSessionId(socket.output());
        final Session session = SessionManager.getInstance().findSession(sessionId);

        assertThat(session).isNotNull();
        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
        assertThat(((User) session.getAttribute("user")).getAccount()).isEqualTo("gugu");
    }

    private String extractSessionId(final String response) {
        return Arrays.stream(response.split("\\r\\n"))
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();
    }
}
