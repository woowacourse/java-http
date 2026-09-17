package org.apache.coyote.http11;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("text/html", "Hello world!");

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

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("text/html", readResource("static/index.html"));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("text/css", readResource("static/css/styles.css"));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String queryString = "account=gugu&password=password";

        // when
        final LoginResult result = requestLogin(queryString);

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(result.response()).isEqualTo(expected);
        assertThat(result.loggingEvents())
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains("login user: User{id=1, account='gugu'"));
    }

    @Test
    void loginFailure() throws IOException {
        // given
        final String queryString = "account=gugu&password=invalid";

        // when
        final LoginResult result = requestLogin(queryString);

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(result.response()).isEqualTo(expected);
        assertThat(result.loggingEvents())
                .extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(message -> message.startsWith("login user:"));
    }

    @Test
    void emptyRequest() {
        // given
        final StubSocket socket = new StubSocket("");
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEmpty();
    }

    @Test
    void invalidQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=% HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse(
                "400 Bad Request",
                "text/plain",
                "잘못된 쿼리 스트링입니다."
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void unsupportedMethod() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse(
                "400 Bad Request",
                "text/plain",
                "지원하지 않는 HTTP 메서드입니다: POST"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    private LoginResult requestLogin(String queryString) {
        final String httpRequest = String.join("\r\n",
                "GET /login?" + queryString + " HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        final ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Http11Processor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            processor.process(socket);
        } finally {
            logger.detachAppender(appender);
        }

        return new LoginResult(socket.output(), List.copyOf(appender.list));
    }

    private String readResource(String resourceName) throws IOException {
        Class<?> testClass = getClass();
        ClassLoader classLoader = testClass.getClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(resourceName);
        InputStream inputStream = Objects.requireNonNull(resourceStream);

        try (inputStream) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String expectedResponse(String contentType, String responseBody) {
        return expectedResponse("200 OK", contentType, responseBody);
    }

    private String expectedResponse(String status, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private record LoginResult(String response, List<ILoggingEvent> loggingEvents) {
    }
}
