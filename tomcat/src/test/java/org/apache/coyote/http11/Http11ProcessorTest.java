package org.apache.coyote.http11;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
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

    @Test
    void css() throws IOException {
        final var socket = new StubSocket(
                "GET /css/styles.css HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream("static/css/styles.css")) {

            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();
            final String[] response = socket.output().split("\r\n\r\n", 2);

            assertThat(response).hasSize(2);
            assertThat(response[0].split("\r\n")).contains(
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + expectedBody.length + " ");
            assertThat(response[1])
                    .isEqualTo(new String(expectedBody, StandardCharsets.UTF_8));
        }
    }

    @Test
    void login() throws IOException {
        assertHtmlResponse("/login", "static/login.html");
    }

    @Test
    void postLoginRedirectsToIndex() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=password"));

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void postLoginWithWrongPasswordRedirectsToUnauthorized() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=wrong"));

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /401.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void unauthorizedPage() throws IOException {
        final var socket = new StubSocket(
                "GET /401.html HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream("static/401.html")) {
            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();

            final String expected =
                    "HTTP/1.1 200 OK \r\n"
                            + "Content-Type: text/html;charset=utf-8 \r\n"
                            + "Content-Length: " + expectedBody.length + " \r\n"
                            + "\r\n"
                            + new String(expectedBody, StandardCharsets.UTF_8);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Test
    void getLoginWithQueryDoesNotAuthenticate() throws IOException {
        assertHtmlResponse("/login?account=gugu&password=password", "static/login.html");
    }

    @Test
    void postLoginReadsHeadersUntilBlankLine() {
        final String request = postLoginRequest("account=gugu&password=password")
                .replace("Content-Length:", "content-length:")
                .replace("\r\n\r\n", "\r\nX-Note: a:b\r\n\r\n");

        assertRedirect(new StubSocket(request), "/index.html");
    }

    @Test
    void postLoginReadsExactlyContentLengthBytes() {
        final String body = "note=가&account=gugu&password=password";
        final var socket = new StubSocket(postLoginRequest(body) + "XX");

        assertRedirect(socket, "/index.html");
    }

    @Test
    void postLoginReadsFragmentedInput() {
        final String request = postLoginRequest("account=gugu&password=password");
        final var socket = new StubSocket(request) {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)) {
                    @Override
                    public synchronized int read(byte[] bytes, int offset, int length) {
                        return super.read(bytes, offset, Math.min(length, 3));
                    }
                };
            }
        };

        assertRedirect(socket, "/index.html");
    }

    @Test
    void truncatedPostBodyIsNotProcessed() {
        final String request = postLoginRequest("account=gugu&password=password");
        final var socket = new StubSocket(request.substring(0, request.length() - 1));

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEmpty();
    }

    @Test
    void registerPage() throws IOException {
        assertHtmlResponse("/register", "static/register.html");
    }

    @Test
    void registeredUserCanLogIn() {
        final String body =
                "account=register-user&password=pass%26word&email=moa%40example.com";

        assertRedirect(new StubSocket(postRequest("/register", body)), "/index.html");

        assertThat(InMemoryUserRepository.findByAccount("register-user"))
                .hasValueSatisfying(user ->
                        assertThat(user).usingRecursiveComparison()
                                .isEqualTo(new User(
                                        "register-user", "pass&word", "moa@example.com"
                                )));

        assertRedirect(
                new StubSocket(postLoginRequest("account=register-user&password=pass%26word")),
                "/index.html"
        );
    }

    @Test
    void registrationWithoutPasswordIsNotSaved() {
        final var socket = new StubSocket(postRequest(
                "/register",
                "account=incomplete-register-user&email=moa%40example.com"
        ));

        new Http11Processor(socket).process(socket);

        assertThat(InMemoryUserRepository.findByAccount("incomplete-register-user"))
                .isEmpty();
        assertThat(socket.output()).isEmpty();
    }

    private String postLoginRequest(String body) {
        return postRequest("/login", body);
    }

    private String postRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private void assertRedirect(StubSocket socket, String location) {
        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: " + location + "\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    private void assertHtmlResponse(String requestTarget, String resourceName) throws IOException {
        final var socket = new StubSocket(
                "GET " + requestTarget + " HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream(resourceName)) {

            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();
            final String[] response = socket.output().split("\r\n\r\n", 2);

            assertThat(response).hasSize(2);
            assertThat(response[0].split("\r\n")).contains(
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + expectedBody.length + " ");
            assertThat(response[1])
                    .isEqualTo(new String(expectedBody, StandardCharsets.UTF_8));
        }
    }

    private List<String> loginSuccessLogs(String body) {
        final Logger logger =
                (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);

        try {
            final var socket = new StubSocket(postLoginRequest(body));

            new Http11Processor(socket).process(socket);

            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .filter(message -> message.startsWith("회원 조회 성공: "))
                    .toList();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void matchingCredentialsLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu&password=password"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void wrongPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu&password=wrong"))
                .isEmpty();
    }

    @Test
    void encodedBodyInDifferentOrderLogsSuccess() {
        assertThat(loginSuccessLogs("password=pass%77ord&account=%67ugu"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void missingPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu"))
                .isEmpty();
    }
}
