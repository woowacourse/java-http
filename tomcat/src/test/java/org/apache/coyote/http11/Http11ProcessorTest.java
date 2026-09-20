package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .contains("Set-Cookie: JSESSIONID=")
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("Content-Length: 12 ")
                .endsWith("\r\n\r\nHello world!");
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .contains("Set-Cookie: JSESSIONID=")
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("Content-Length: 5564 ")
                .endsWith("\r\n\r\n" + body);
    }

    @Test
    void 요청에_JSESSIONID가_있으면_SetCookie를_반환하지_않는다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .doesNotContain("Set-Cookie");
    }
}
