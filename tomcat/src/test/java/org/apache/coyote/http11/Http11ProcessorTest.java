package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.Dispatcher;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static Dispatcher dispatcher() {
        return new Dispatcher(
                new ControllerMapping(Map.of(
                        "/", new HomeController(),
                        "/login", new LoginController(),
                        "/register", new RegisterController()
                )),
                new SessionManager()
        );
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: 12 \r\n")
                .endsWith("Hello world!");
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
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: 5564 \r\n")
                .endsWith(expectedBody);
    }

    @Test
    void css() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8 \r\n");
    }

    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: " + body.length + " \r\n")
                .endsWith(new String(body, StandardCharsets.UTF_8));
    }

    @Test
    void login_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void login_fail() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive",
                "Content-Length: 35 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=wrongPassword");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /401.html \r\n");
    }

    @Test
    void registerPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: " + body.length + " \r\n")
                .endsWith(new String(body, StandardCharsets.UTF_8));
    }

    @Test
    void register_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive",
                "Content-Length: 51 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=hong&email=hong@woowa.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, dispatcher());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .contains("Set-Cookie: JSESSIONID=");
        assertThat(InMemoryUserRepository.findByAccount("hong")).isPresent();
    }
}
