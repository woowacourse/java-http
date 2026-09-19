package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    void 로그인에_성공하면_인덱스_페이지를_응답한다() throws IOException {
        // given
        final var request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var responseBody = readResource("static/index.html");
        final var expected = createResponse(
                "HTTP/1.1 200 OK ",
                responseBody
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인에_실패하면_인증_실패_페이지를_응답한다() throws IOException {
        // given
        final var request = String.join("\r\n",
                "GET /login?account=gugu&password=wrong HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var responseBody = readResource("static/401.html");
        final var expected = createResponse(
                "HTTP/1.1 200 OK ",
                responseBody
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String readResource(final String path) throws IOException {
        final var resource = getClass()
                .getClassLoader()
                .getResourceAsStream(path);

        if (resource == null) {
            throw new FileNotFoundException(path);
        }

        try (resource) {
            return new String(
                    resource.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    private String createResponse(
            final String statusLine,
            final String responseBody
    ) {
        final var contentLength = responseBody
                .getBytes(StandardCharsets.UTF_8)
                .length;

        return String.join("\r\n",
                statusLine,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                responseBody
        );
    }
}
