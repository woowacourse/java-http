package org.apache.coyote.http11;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.coyote.http11.pageController.LoginController;
import org.apache.coyote.http11.pageController.PageController;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
    private static final String SET_SESSION_COOKIE =
            "\r\nSet-Cookie: JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12} \r\n";

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
        final String body = "account=gugu&password=password";

        // when
        final LoginResult result = requestLogin(body);

        // then
        assertThat(result.response())
                .startsWith("HTTP/1.1 302 Found \r\nLocation: /index.html \r\n")
                .containsPattern(SET_SESSION_COOKIE);
        assertThat(result.loggingEvents())
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains("login user: User{id=1, account='gugu'"));
    }

    @Test
    void loginFailure() throws IOException {
        // given
        final String body = "account=gugu&password=invalid";

        // when
        final LoginResult result = requestLogin(body);

        // then
        assertThat(result.response()).isEqualTo(redirectResponse("/401.html"));
        assertThat(result.response()).doesNotContain("Set-Cookie");
        assertThat(result.loggingEvents())
                .extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(message -> message.startsWith("login user:"));
    }

    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void queryStringDoesNotLogin() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final LoginResult result = requestLoginWith(httpRequest);

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(result.response()).isEqualTo(expected);
        assertThat(result.loggingEvents())
                .extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(message -> message.startsWith("login user:"));
    }

    @Test
    void loginPageWhenValuesAreEmpty() throws IOException {
        // when
        final LoginResult result = requestLogin("account=&password=");

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(result.response()).isEqualTo(expected);
    }

    @Test
    void loginFailureWithUnknownAccount() {
        // when
        final LoginResult result = requestLogin("account=unknown&password=password");

        // then
        assertThat(result.response()).isEqualTo(redirectResponse("/401.html"));
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
                "PUT /index.html HTTP/1.1",
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
                "지원하지 않는 HTTP 메서드입니다: PUT"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void methodNotAllowedForStaticResource() {
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
                "405 Method Not Allowed",
                "text/plain",
                "지원하지 않는 HTTP 메서드입니다: POST"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void invalidHeader() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host localhost",
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
                "잘못된 http 헤더 형태입니다: Host localhost"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginWithAbsoluteForm() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET http://localhost:8080/login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("text/html", readResource("static/login.html"));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void targetWithoutSchemeAndLeadingSlash() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET localhost:8080/login HTTP/1.1",
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
                "잘못된 요청 대상입니다: localhost:8080/login"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /nothing.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = expectedResponse("404 Not Found", "text/html", readResource("static/404.html"));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void internalServerErrorWhenControllerThrowsIOException() throws IOException {
        // given
        final Http11Processor processor = new Http11Processor(new StubSocket());
        final PageController controller = request -> {
            throw new IOException("파일을 읽을 수 없습니다.");
        };

        // when
        final HttpResponse response = processor.handle(indexRequest(), controller);

        // then
        final String expected = expectedResponse("500 Internal Server Error", "text/html", readResource("static/500.html"));
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void internalServerErrorWhenControllerThrowsRuntimeException() throws IOException {
        // given
        final Http11Processor processor = new Http11Processor(new StubSocket());
        final PageController controller = request -> {
            throw new IllegalStateException("예상하지 못한 오류");
        };

        // when
        final HttpResponse response = processor.handle(indexRequest(), controller);

        // then
        final String expected = expectedResponse("500 Internal Server Error", "text/html", readResource("static/500.html"));
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void badRequestWhenControllerThrowsBadRequestException() {
        // given
        final Http11Processor processor = new Http11Processor(new StubSocket());
        final PageController controller = request -> {
            throw new BadRequestException("잘못된 정적 리소스 경로입니다: /../secret");
        };

        // when
        final HttpResponse response = processor.handle(indexRequest(), controller);

        // then
        final String expected = expectedResponse(
                "400 Bad Request",
                "text/plain",
                "잘못된 정적 리소스 경로입니다: /../secret"
        );
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    private HttpRequest indexRequest() {
        return HttpRequest.from("GET /index.html HTTP/1.1", HttpHeaders.empty(), HttpBody.empty());
    }

    @Test
    void noSetCookieForStaticResource() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    private LoginResult requestLogin(String body) {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
        return requestLoginWith(httpRequest);
    }

    private LoginResult requestLoginWith(String httpRequest) {
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        final ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LoginController.class);
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

    private String redirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
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
