package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var requestMapping = createRequestMapping();
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12",
                "",
                "Hello world!");

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
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

        final var requestMapping = createRequestMapping();
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var requestMapping = createRequestMapping();
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: 211991\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loginSuccess() {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var requestMapping = createRequestMapping();
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /index.html\r\n"
                + "Content-Length: 0\r\n"
                + "Set-Cookie: JSESSIONID=session-id\r\n"
                + "\r\n";
        String actual = socket.output().replaceAll(
                "JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "JSESSIONID=session-id");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void registerSuccess() {
        // given
        String account = "tory";
        String password = "password";
        String requestBody = "account=" + account + "&password=" + password + "&email=test@example.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        final var requestMapping = createRequestMapping();
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, requestMapping);
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();

        // when
        processor.process(socket);

        // then
        String expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /index.html\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n";
        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount(account)).hasValueSatisfying(user -> {
            assertThat(user.getAccount()).isEqualTo(account);
            assertThat(user.checkPassword(password)).isTrue();
        });
    }

    private RequestMapping createRequestMapping() {
        Map<String, Controller> controllersByPath = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new StaticResourceController());
        return new RequestMapping(controllersByPath);
    }
}
