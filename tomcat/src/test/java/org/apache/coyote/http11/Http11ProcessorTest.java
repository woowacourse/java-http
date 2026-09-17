package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 루트_경로로_요청하면_index_html을_응답한다() throws Exception {
        final var socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final String content = readResource("static/index.html");
        final String expected = response("200 OK", "text/html", content);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_경로로_요청하면_login_html을_응답한다() throws Exception {
        final var socket = new StubSocket("GET /login?account=gugu&password=password HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final String content = readResource("static/login.html");
        final String expected = response("200 OK", "text/html", content);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_경로로_요청하면_404를_응답한다() {
        final var socket = new StubSocket("GET /not-found HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final String expected = response("404 Not Found", "text/html", "<h1>404 Not Found</h1>");
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String readResource(final String name) throws Exception {
        final URL resource = getClass().getClassLoader().getResource(name);
        assertThat(resource).isNotNull();
        return Files.readString(Path.of(resource.toURI()), StandardCharsets.UTF_8);
    }

    private String response(final String status, final String contentType, final String content) {
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length,
                "",
                content);
    }
}
