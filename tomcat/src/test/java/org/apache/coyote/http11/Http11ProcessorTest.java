package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

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
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output().getBytes())
                .contains("HTTP/1.1 200 OK".getBytes())
                .contains("Content-Length: 5670".getBytes())
                .contains("Content-Type: text/html;charset=utf-8".getBytes())
                .contains(body.getBytes());
    }
}
