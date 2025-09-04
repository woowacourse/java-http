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

        // 윈도우 환경에서 줄 바꿈 형식에 차이가 존재해 테스트 코드를 아래과 같이 변경을 하였음

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "\r\n"+
                content;

        assertThat(socket.output()).isEqualTo(expected);
    }
}
