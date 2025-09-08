package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    /**
     * path 없이 접근을 할 때는 index.html로 접근하도록 LMS에 적혀있어서 테스트 주석 처리
     */

    @Disabled
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

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
        final String output = socket.output();
        final String[] lines = output.split("\r\n");

        // 개별 헤더 검증
        assertThat(lines[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(output).contains("Content-Type: text/html;charset=utf-8");

        // 응답 본문 검증
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectedContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(output).contains(expectedContent);
    }

    @Test
    void invalidRequestStartLine_ShouldReturn400BadRequest() {
        // given
        final String invalidHttpRequest = String.join("\r\n",
                "", // 빈 시작줄
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(invalidHttpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        final String[] lines = output.split("\r\n");
        final String responseStartLine = lines[0];
        
        assertThat(responseStartLine).isEqualTo("HTTP/1.1 400 Bad Request ");
    }
}
