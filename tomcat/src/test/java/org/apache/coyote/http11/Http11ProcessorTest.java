package org.apache.coyote.http11;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
        assertLoginResponse("/login");
    }

    @Test
    void successfulLoginRedirectsToIndex() {
        final var socket = new StubSocket(
                "GET /login?account=gugu&password=password HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    private void assertLoginResponse(String requestTarget) throws IOException {
        final var socket = new StubSocket(
                "GET " + requestTarget + " HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream("static/login.html")) {

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

    private List<String> loginSuccessLogs(String requestTarget) {
        final Logger logger =
                (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);

        try {
            final var socket = new StubSocket(
                    "GET " + requestTarget + " HTTP/1.1\r\nHost: localhost\r\n\r\n");

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
        assertThat(loginSuccessLogs("/login?account=gugu&password=password"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void wrongPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("/login?account=gugu&password=wrong"))
                .isEmpty();
    }

    @Test
    void encodedQueryInDifferentOrderLogsSuccess() {
        assertThat(loginSuccessLogs("/login?password=pass%77ord&account=%67ugu"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void missingPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("/login?account=gugu"))
                .isEmpty();
    }
}
