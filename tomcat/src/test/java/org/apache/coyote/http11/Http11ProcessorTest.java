package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void successfulLoginRedirectsToIndex() {
        assertLoginRedirect("account=gugu&password=password", "/index.html");
    }

    @Test
    void failedLoginRedirectsToUnauthorizedPage() {
        assertLoginRedirect("account=gugu&password=wrong", "/401.html");
        assertLoginRedirect("account=unknown&password=password", "/401.html");
        assertLoginRedirect("account=gugu", "/401.html");
        assertLoginRedirect("password=password", "/401.html");
        assertLoginRedirect("account=&password=", "/401.html");
    }

    @Test
    void loginWithoutCredentialsServesLoginPage() throws IOException {
        final var socket = new StubSocket("GET /login HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n")
                    .doesNotContain("Location:")
                    .endsWith(new String(resource.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private void assertLoginRedirect(String body, String location) {
        final var socket = new StubSocket(postRequest("/login", body));

        new Http11Processor(socket).process(socket);

        assertRedirect(socket, location);
    }

    @Test
    void registerPageIsServedForGetRequest() throws IOException {
        final var socket = new StubSocket("GET /register HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader().getResourceAsStream("static/register.html")) {
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n")
                    .endsWith(new String(resource.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @Test
    void postRegisterSavesUserAndRedirectsToIndex() {
        String body = "account=new-user&password=pass%3Dword&email=user%40example.com";
        final var socket = new StubSocket(postRequest("/register", body));

        new Http11Processor(socket).process(socket);

        assertRedirect(socket, "/index.html");
        assertThat(InMemoryUserRepository.findByAccount("new-user"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("pass=word")).isTrue());
    }

    private String postRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(java.nio.charset.StandardCharsets.UTF_8).length,
                "",
                body
        );
    }

    private void assertRedirect(StubSocket socket, String location) {
        assertThat(socket.output()).isEqualTo("HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void missingResourceReturnsNotFound() {
        final var socket = new StubSocket("GET /missing.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 9 ",
                "",
                "Not Found");
        assertThat(socket.output()).isEqualTo(expected);
    }

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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
