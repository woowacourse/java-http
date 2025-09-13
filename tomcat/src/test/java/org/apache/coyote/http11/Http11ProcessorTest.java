package org.apache.coyote.http11;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final String output = socket.output();

        // then
        assertSoftly(s -> {
            s.assertThat(output).contains("HTTP/1.1 200 OK");
            s.assertThat(output).contains("Content-Type: text/html;charset=utf-8");
            s.assertThat(output).contains("Content-Length: 12");
            s.assertThat(output).contains("Hello world!");
        });
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final String output = socket.output();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertSoftly(s -> {
            s.assertThat(output).contains("HTTP/1.1 200 OK");
            s.assertThat(output).contains("Content-Type: text/html;charset=utf-8");
            s.assertThat(output).contains("Content-Length: 5670");
            s.assertThat(output).contains(expectedBody);
        });
    }
}
