package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.api.RequestMapping;
import com.techcourse.api.RequestMappingFactory;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.connector.CatalinaHttpHandler;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final Manager sessionManager = SessionManager.getInstance();
    private final RequestMapping requestMapping = RequestMappingFactory.create(sessionManager);
    private final HttpHandler httpHandler = new CatalinaHttpHandler(sessionManager, requestMapping);

    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void set_cookie_when_request_cookie_has_no_session_id() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void does_not_set_cookie_when_request_has_session_id() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session-id ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
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
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void css_uses_ok_response_with_requested_resource() throws IOException {
        final var socket = new StubSocket("GET /css/styles.css HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, httpHandler);

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/css;charset=utf-8\r\n")
                .contains("Content-Length: " + expectedBody.length + "\r\n")
                .endsWith(new String(expectedBody, StandardCharsets.UTF_8));
    }

    @Test
    void login_success_redirect_with_post() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of(
                "HTTP/1.1 302 FOUND",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void logged_in_user_redirect_to_index_with_get() throws IOException {
        // given
        final Session session = sessionManager.createSession("logged-in-session-id");
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of(
                "HTTP/1.1 302 FOUND",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
        sessionManager.remove(session);
    }

    @Test
    void login_fail_redirect_with_post() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 27 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=wrong");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        var expected = List.of(
                "HTTP/1.1 401 UNAUTHORIZED",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 2426",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void register_with_post() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        Optional<User> user = InMemoryUserRepository.findByAccount("gugu");
        assertThat(user.isPresent()).isTrue();
        assertThat(user.get().toString()).contains("email='hkkang@woowahan.com'");
    }

    @Test
    void register_success_redirect() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, httpHandler);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).contains(expected);
    }
}
