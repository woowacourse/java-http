package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Disabled("Hello wolrd! 대신 index.html 화면을 기본으로 반환하도록 변경하였으므로 테스트가 실패함")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var sessionManager = new SessionManager();
        final var processor = new Http11Processor(socket, sessionManager);

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
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var sessionManager = new SessionManager();
        final var processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectedBody = Files.readString(new File(resource.getFile()).toPath());

        final String responseResult = socket.output();
        assertThat(responseResult).startsWith("HTTP/1.1 200 OK");
        assertThat(responseResult).contains("Content-Type: text/html;charset=utf-8");
        assertThat(responseResult).contains("Content-Length: " + expectedBody.getBytes(StandardCharsets.UTF_8).length);
        assertThat(responseResult).contains("Set-Cookie: JSESSIONID=");

        assertThat(responseResult).endsWith(expectedBody);
    }
}
