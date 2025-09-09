package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

        // then
//        var expected = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 12 ",
//                "",
//                "Hello world!");

        String output = socket.output();

        // 상태 라인 검증
        assertThat(output).startsWith("HTTP/1.1 200 OK ");

        // 각 헤더가 존재하는지 검증 (순서 무관) -> 헤더 순서 상관 없이 구현해서, 테스트 변경했습니다.
        assertThat(output).contains("Content-Type: text/html;charset=utf-8 ");
        assertThat(output).contains("Content-Length: 12 ");

        // 바디 검증
        assertThat(output).endsWith("Hello world!");
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
//        var expected = "HTTP/1.1 200 OK \r\n" +
//                "Content-Type: text/html;charset=utf-8 \r\n" +
//                "Content-Length: 5564 \r\n" +
//                "\r\n"+
//                new String(Files.readAllBytes(Path.of(URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8))));
        // URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8) 경로에 한글이 있어서, 디코딩하도록 테스트 수정했습니다.

        String output = socket.output();

        // 상태 라인 검증
        assertThat(output).startsWith("HTTP/1.1 200 OK \r\n");

        // 각 헤더가 존재하는지 검증 (순서 무관) -> 헤더 순서 상관 없이 구현해서, 테스트 변경했습니다.
        assertThat(output).contains("Content-Type: text/html;charset=utf-8 \r\n");
        assertThat(output).contains("Content-Length: 5564 \r\n");

        // 바디 검증
        assertThat(output).endsWith(new String(Files.readAllBytes(Path.of(URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8)))));
    }
}
