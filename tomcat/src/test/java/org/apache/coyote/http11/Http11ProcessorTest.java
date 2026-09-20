package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

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
    void css() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/css/styles.css"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("css/styles.css", "text/css;charset=utf-8"));
    }

    @Test
    void notFound() {
        // given
        final var socket = new StubSocket(getRequest("/nothing.html"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(errorResponse(HttpStatus.NOT_FOUND));
    }

    @Test
    void loginPageWithoutParameters() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/login"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("login.html", "text/html;charset=utf-8"));
    }

    @Test
    void loginSuccessRedirectsToIndex() {
        // given
        final var socket = new StubSocket(getRequest("/login?account=gugu&password=password"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void loginFailureRedirectsToUnauthorized() {
        // given
        final var socket = new StubSocket(getRequest("/login?account=gugu&password=wrong"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/401.html"));
    }

    @Test
    void registerPage() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/register"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("register.html", "text/html;charset=utf-8"));
    }

    @Test
    void registerSavesUserAndRedirectsToIndex() {
        // given
        final String body = "account=tester&password=secret&email=tester%40woowahan.com";
        final var socket = new StubSocket(postRequest("/register", body));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));

        final Optional<User> saved = InMemoryUserRepository.findByAccount("tester");
        assertThat(saved).isPresent();
        assertThat(saved.get().checkPassword("secret")).isTrue();
        assertThat(saved.get().toString()).contains("tester@woowahan.com");
    }

    @Test
    void registerWithMissingParameterRespondsBadRequest() {
        // given
        final String body = "account=noemail&password=secret";
        final var socket = new StubSocket(postRequest("/register", body));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(errorResponse(HttpStatus.BAD_REQUEST));
        assertThat(InMemoryUserRepository.findByAccount("noemail")).isEmpty();
    }

    @Test
    void registerWithLowerCaseHeaderNames() {
        // given
        final String body = "account=lower&password=secret&email=lower%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "host: localhost:8080 ",
                "content-type: application/x-www-form-urlencoded ",
                "content-length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
        assertThat(InMemoryUserRepository.findByAccount("lower")).isPresent();
    }

    private String getRequest(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    private String postRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);
    }

    private String staticResponse(String resourceName, String contentType) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: " + contentType + " \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body, StandardCharsets.UTF_8);
    }

    private String redirectResponse(String location) {
        return "HTTP/1.1 302 Found \r\n" +
                "Location: " + location + " \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
    }

    private String errorResponse(HttpStatus status) {
        return "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
    }
}
