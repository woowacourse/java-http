package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            assertThat(resource).isNotNull();

            final var responseBody = resource.readAllBytes();
            final var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody, StandardCharsets.UTF_8)
            );

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Test
    void 정적_리소스에_맞는_Content_Type을_응답한다() {
        final var contentTypes = Map.of(
                "/css/styles.css", "text/css",
                "/js/scripts.js", "text/javascript",
                "/assets/img/error-404-monochrome.svg", "image/svg+xml"
        );

        contentTypes.forEach((path, contentType) -> {
            final var httpRequest = String.join("\r\n",
                    "GET " + path + " HTTP/1.1",
                    "Host: localhost:8080",
                    "",
                    ""
            );
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .as(path)
                    .contains("Content-Type: " + contentType);
        });
    }
}
