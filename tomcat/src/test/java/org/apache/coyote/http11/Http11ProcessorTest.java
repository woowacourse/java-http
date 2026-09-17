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
        final var socket = new StubSocket();
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
        final var socket = new StubSocket("GET /login HTTP/1.1\r\n\r\n");
        final var processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(createResponse(
                "text/html;charset=utf-8", readResource("static/login.html")));
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
