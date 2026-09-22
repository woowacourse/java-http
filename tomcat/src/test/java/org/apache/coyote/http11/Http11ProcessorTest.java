package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
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
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
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
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " \r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPage() throws IOException {
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " \r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccess() {
        final String body = "account=gugu&password=password";
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");

        final String sessionId = socket.output().split("Set-Cookie: JSESSIONID=", 2)[1].split(" ", 2)[0];
        assertThat(SessionManager.findSession(sessionId).getAttribute("user")).isNotNull();
    }

    @Test
    void loginFail() {
        final String body = "account=gugu&password=wrong";
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /401.html ");
    }

    @Test
    void register() {
        final String body = "account=dongkey&password=password&email=dongkey%40woowahan.com";
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
        final var saved = InMemoryUserRepository.findByAccount("dongkey");
        assertThat(saved).isPresent();
        assertThat(saved.get().toString()).contains("dongkey@woowahan.com");
    }

    @Test
    void setCookieWhenNoSessionId() {
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void loginPageWhenLoggedIn() {
        final Session session = new Session("logged-in-session");
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        SessionManager.add(session);
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=logged-in-session ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
    }

    @Test
    void loginSuccessSetsSessionCookie() {
        final String body = "account=gugu&password=password";
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("Set-Cookie: JSESSIONID=")
                .doesNotContain("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
