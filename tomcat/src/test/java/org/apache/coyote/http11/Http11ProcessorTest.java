package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final Session session = SessionManager.createSession();

        final String rootRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                ""
        );

        final var rootSocket = new StubSocket(rootRequest);

        final String indexRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                ""
        );
        final var indexSocket = new StubSocket(indexRequest);

        // when
        new Http11Processor(rootSocket).process(rootSocket);
        new Http11Processor(indexSocket).process(indexSocket);

        // then
        assertThat(rootSocket.output()).isEqualTo(indexSocket.output());
    }

    @Test
    void index() throws IOException {
        // given
        final Session session = SessionManager.createSession();

        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        final byte[] expectedBody = Files.readAllBytes(
                new File(resource.getFile()).toPath()
        );

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + expectedBody.length + " \r\n" +
                "\r\n" +
                new String(
                        expectedBody, StandardCharsets.UTF_8
                );

        assertThat(socket.output()).isEqualTo(expected);
    }
}
