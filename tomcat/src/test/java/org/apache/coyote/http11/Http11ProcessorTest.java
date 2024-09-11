package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {


    @DisplayName("GET / 요청 시 /index.html로 리다이렉트")
    @Test
    void defaultPath_RedirectToIndex() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found\r\n",
                "Location: /index.html\r\n"
        );
    }

    @DisplayName("GET /index.html 요청 시 index.html 내용 응답")
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK\r\n",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: 5564\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}
