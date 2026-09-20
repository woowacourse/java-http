package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.handler.StaticResourceHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.handler.ResourceResolver;
import org.apache.coyote.Adapter;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final Adapter ADAPTER = new ResourceResolver(List.of(
            new StaticResourceHandler()
    ));

    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, ADAPTER);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expectedResponse("static/index.html"));
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
        final Http11Processor processor = new Http11Processor(socket, ADAPTER);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expectedResponse("static/index.html"));
    }

    private String expectedResponse(final String resourcePath) throws IOException {
        final byte[] body = readResource(resourcePath);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html;charset=utf-8\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n"
                + new String(body, StandardCharsets.UTF_8);
    }

    private byte[] readResource(final String resourcePath) throws IOException {
        try (final InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            return inputStream.readAllBytes();
        }
    }
}
