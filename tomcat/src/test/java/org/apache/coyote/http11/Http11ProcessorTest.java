package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        final String body = "Hello world!";
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void requestLineWithExtraPartIsRejected() {
        final var socket = new StubSocket("GET /index.html HTTP/1.1 EXTRA\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).isEmpty();
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
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            assertThat(resource).isNotNull();
            final byte[] html = resource.readAllBytes();
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + html.length + " ",
                    "",
                    new String(html, StandardCharsets.UTF_8));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Test
    void css() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/css/styles.css")) {
            assertThat(resource).isNotNull();
            final byte[] css = resource.readAllBytes();
            final String expectedHeaders = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + css.length + " ",
                    "",
                    "");

            final String actual = socket.output();
            assertThat(actual).startsWith(expectedHeaders);
            assertThat(actual.substring(expectedHeaders.length()))
                    .isEqualTo(new String(css, StandardCharsets.UTF_8));
        }
    }

    @Test
    void javascriptFiles() throws IOException {
        final var paths = List.of(
                "/js/scripts.js",
                "/assets/chart-area.js",
                "/assets/chart-bar.js",
                "/assets/chart-pie.js");

        for (final String path : paths) {
            final var socket = new StubSocket("GET " + path + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
            final var processor = new Http11Processor(socket);

            processor.process(socket);

            try (final var resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
                assertThat(resource).isNotNull();
                final byte[] javascript = resource.readAllBytes();
                final String expected = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/javascript;charset=utf-8 ",
                        "Content-Length: " + javascript.length + " ",
                        "",
                        new String(javascript, StandardCharsets.UTF_8));

                assertThat(socket.output()).as(path).isEqualTo(expected);
            }
        }
    }

    @Test
    void loginPageIsReturnedWithOrWithoutQueryString() throws IOException {
        final var requestTargets = List.of(
                "/login",
                "/login?account=gugu&password=password");

        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(resource).isNotNull();
            final byte[] html = resource.readAllBytes();
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + html.length + " ",
                    "",
                    new String(html, StandardCharsets.UTF_8));

            for (final String requestTarget : requestTargets) {
                final var socket = new StubSocket("GET " + requestTarget + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
                final var processor = new Http11Processor(socket);

                processor.process(socket);

                assertThat(socket.output()).as(requestTarget).isEqualTo(expected);
            }
        }
    }
}
