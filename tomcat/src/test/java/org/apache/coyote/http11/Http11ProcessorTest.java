package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

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
        String response = socket.output();

        assertThat(response).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(response).containsPattern(
                "Set-Cookie: JSESSIONID=[0-9a-f-]{36}; Path=/\\r\\n"
        );
        assertThat(response).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(response).contains("Content-Length: 12\r\n");
        assertThat(responseBody(response)).isEqualTo("Hello world!");
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
        final String expectedBody = new String(
                Files.readAllBytes(new File(resource.getFile()).toPath()),
                StandardCharsets.UTF_8
        );
        final String response = socket.output();

        assertThat(response).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(response).containsPattern(
                "Set-Cookie: JSESSIONID=[0-9a-f-]{36}; Path=/\\r\\n"
        );
        assertThat(response).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(response).contains(
                "Content-Length: " + expectedBody.getBytes(StandardCharsets.UTF_8).length + "\r\n"
        );
        assertThat(responseBody(response)).isEqualTo(expectedBody);
    }

    private static String responseBody(final String response) {
        return response.substring(response.indexOf("\r\n\r\n") + 4);
    }
}
