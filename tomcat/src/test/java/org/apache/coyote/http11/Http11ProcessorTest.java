package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException, URISyntaxException {
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
        // 프로젝트 경로에 공백, 한글, 특수문자 등이 있을 경우
        // resource.getFile()은 URL 인코딩된 문자열을 그대로 반환하여
        // 파일 시스템에서 해당 경로를 찾을 수 없어 NoSuchFileException 발생.
        // resource.toURI()를 통해 URI 객체로 변환하면
        // Paths.get()에서 내부적으로 URL 디코딩이 수행되어 올바른 경로 생성.
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        Path path = Paths.get(resource.toURI());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
