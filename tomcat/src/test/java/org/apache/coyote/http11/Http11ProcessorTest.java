package org.apache.coyote.http11;

import com.techcourse.controller.ApplicationController;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationDispatcher;
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
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEmpty();
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/index.html");
        var expected = createResponse("text/html;charset=utf-8", responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginFail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong-password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var service = new ApplicationService();
        final var controller = new ApplicationController(service);
        final var dispatcher = new ApplicationDispatcher(controller);
        final var processor = new Http11Processor(socket, dispatcher);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/login.html");
        var expected = createResponse("text/html;charset=utf-8", responseBody);

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
        final var processor = new Http11Processor(socket);

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
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var service = new ApplicationService();
        final var controller = new ApplicationController(service);
        final var dispatcher = new ApplicationDispatcher(controller);
        final var processor = new Http11Processor(socket, dispatcher);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/login.html");
        var expected = createResponse("text/html;charset=utf-8", responseBody);

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
        final var processor = new Http11Processor(socket);

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

    private String readResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(
                    Objects.requireNonNull(resource).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
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
