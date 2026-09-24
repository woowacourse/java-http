package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final String SESSION_ID = "12345678-1234-1234-1234-123456789abc";

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_WhenFirst_ThenLoginPage() throws IOException {
        String fixedUuid = UUID.fromString(SESSION_ID).toString();

        SessionIdGenerator generator = mock(SessionIdGenerator.class);
        when(generator.generate()).thenReturn(fixedUuid);

        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, generator);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3804 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_Success() {
        String fixedUuid = UUID.fromString(SESSION_ID).toString();

        SessionIdGenerator generator = mock(SessionIdGenerator.class);
        when(generator.generate()).thenReturn(fixedUuid);

        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, generator);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "Set-Cookie: JSESSIONID=" + SESSION_ID + " ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_WhenInvalidAccount_Redirect401() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 33",
                "",
                "account=invalid&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /401.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_WhenInvalidPassword_Redirect401() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "",
                "account=gugu&password=invalid");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /401.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_Success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 49",
                "",
                "account=gugu&email=gugu@gugu.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_WhenUserHasValidSession_ThenRedirection() {
        // given
        User user = new User("account", "password", "email");
        Session session = new Session(SESSION_ID, "user", user);

        SessionManager manager = SessionManager.getInstance();
        manager.add(session);

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + SESSION_ID + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
        manager.clear();
    }
}
