package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourcePath = "static/index.html";
        final byte[] fileContentBytes;
        try (final InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assert fileInputStream != null;
            fileContentBytes = fileInputStream.readAllBytes();
        }

        final String fileContent = new String(fileContentBytes, StandardCharsets.UTF_8);
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + fileContentBytes.length,
                "",
                fileContent);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourcePath = "static/index.html";
        final URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        Assertions.assertNotNull(resourceUrl);

        final File resourceFile = new File(resourceUrl.getFile());
        final byte[] fileContentBytes = Files.readAllBytes(resourceFile.toPath());
        final String fileContent = new String(fileContentBytes, StandardCharsets.UTF_8);

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                fileContent);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
