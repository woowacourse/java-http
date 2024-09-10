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
    @DisplayName("process 메서드는 요청에 대해 올바른 HTTP 응답을 생성한다.")
    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/hello.html");
        String expectedResponseLine = "HTTP/1.1 200 OK \r\n";
        String expectedContentType = "Content-Type: text/html;charset=utf-8 \r\n";
        String expectedContentLength = "Content-Length: 13 \r\n";
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseLine, expectedContentLength, expectedContentType, expectedResponseBody);
    }
}
