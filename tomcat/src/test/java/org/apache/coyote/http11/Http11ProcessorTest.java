package org.apache.coyote.http11;

import com.techcourse.controller.ApplicationController;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationDispatcher;
import com.techcourse.web.StaticResourceHandler;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.apache.coyote.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void emptyRequest() {
        // given
        final var socket = new StubSocket("");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEmpty();
    }

    @Test
    void badRequest() {
        // given
        final var socket = new StubSocket("GET /index.html\r\n\r\n");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createResponse(
                "400 Bad Request",
                "text/html;charset=utf-8",
                "400 Bad Request"
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\nCookie: JSESSIONID=existing-session\r\n\r\n");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createResponse("text/html;charset=utf-8", "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/index.html");
        var expected = createResponse("text/html;charset=utf-8", responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginFail() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong-password HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createRedirectResponse("/401.html");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(readResource("static/index.html"))
                .contains("<link href=\"css/styles.css\" rel=\"stylesheet\" />");

        String responseBody = readResource("static/css/styles.css");
        var expected = createResponse("text/css;charset=utf-8", responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createRedirectResponse("/index.html");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /not-found.css HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/404.html");
        var expected = createResponse(
                "404 Not Found",
                "text/html;charset=utf-8",
                responseBody
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPage() throws IOException {
        final var socket = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID=existing-session\r\n\r\n");
        final var processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(createResponse(
                "text/html;charset=utf-8", readResource("static/login.html")));
    }

    @Test
    void issuesSessionCookieWhenCookieHeaderIsMissing() {
        final var socket = new StubSocket();

        createProcessor(socket).process(socket);

        assertSessionCookie(socket.output());
    }

    @Test
    void issuesSessionCookieWhenOnlyOtherCookiesExistAndPreservesRedirect() {
        final var socket = new StubSocket(String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry",
                "", ""));

        createProcessor(socket).process(socket);

        assertSessionCookie(socket.output());
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n")
                .contains("\r\nLocation: /index.html\r\n");
    }

    @Test
    void parsesCookiesAndKeepsExistingSession() {
        final var socket = new StubSocket(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=existing-session; token=abc==",
                "content-length: 12",
                "", "account=gugu"));
        final var processor = new Http11Processor(socket, request -> {
            assertThat(request.cookies().getCookie("yummy_cookie")).isEqualTo("choco");
            assertThat(request.cookies().getCookie("JSESSIONID")).isEqualTo("existing-session");
            assertThat(request.cookies().getCookie("token")).isEqualTo("abc==");
            assertThat(request.parameters()).containsEntry("account", "gugu");
            return HttpResponse.redirect("/index.html");
        });

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(createRedirectResponse("/index.html"));
    }

    private void assertSessionCookie(String response) {
        var cookies = response.split("\r\n\r\n", 2)[0].lines()
                .filter(line -> line.startsWith("Set-Cookie: "))
                .toList();
        assertThat(cookies).hasSize(1);
        assertThat(cookies.getFirst()).startsWith("Set-Cookie: JSESSIONID=");
        String sessionId = cookies.getFirst().substring("Set-Cookie: JSESSIONID=".length());
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
    }

    private Http11Processor createProcessor(StubSocket socket) {
        final var service = new ApplicationService();
        final var resourceHandler = new StaticResourceHandler();
        final var controller = new ApplicationController(service);
        final var dispatcher = new ApplicationDispatcher(controller, resourceHandler);

        return new Http11Processor(socket, dispatcher);
    }

    private String readResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(
                    Objects.requireNonNull(resource).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    private String createRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 0",
                "Location: " + location,
                "",
                ""
        );
    }

    private String createResponse(String contentType, String responseBody) {
        return createResponse("200 OK", contentType, responseBody);
    }

    private String createResponse(String status, String contentType, String responseBody) {
        int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType,
                "Content-Length: " + contentLength,
                "",
                responseBody
        );
    }
}
