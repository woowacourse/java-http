package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Hello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        final String responseBody = new String(
                Files.readAllBytes(new File(resource.getFile()).toPath())
        );

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("Set-Cookie: JSESSIONID=")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: 5564")
                .contains(responseBody);
    }

    @Test
    void login_success() {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Connection: keep-alive",
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("302")
                .contains("Location: /index.html");
    }

    @Test
    void login_failure() {
        // given
        final String body = "account=gugu&password=gugu";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Connection: keep-alive",
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("302")
                .contains("Location: /401.html");
    }

    @Test
    void register_success() {
        // given
        final String body = "account=pobi&password=1234&email=pobi@test.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("302")
                .contains("Location: /index.html");

        assertThat(InMemoryUserRepository.findByAccount("pobi"))
                .isPresent();
    }

    @Test
    void jSessionIdIssue_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void jSessionIdIssue_success_already_have_no_issue() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=existing-id",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    void login_success_save_user_in_session() {
        // given
        final String sessionId = "login-session-id";
        final String body = "account=gugu&password=password";

        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session(sessionId);
        sessionManager.add(session);

        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final Session savedSession = sessionManager.findSession(sessionId);
        final User user = (User) savedSession.getAttribute("user");

        assertThat(user).isNotNull();
        assertThat(user.getAccount()).isEqualTo("gugu");

        assertThat(socket.output())
                .contains("302")
                .contains("Location: /index.html");
    }
}
