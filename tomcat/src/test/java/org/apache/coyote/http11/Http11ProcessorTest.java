package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.servlet.StaticResourceServlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.ServletContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    /*
     * LMS 2.CSS 지원하기를 보면, gif에 "/"로 요청해도 index.html을 보여주고 있습니다.
     * 현재 이 프로젝트도 이에 맞게 수정되어있는데,
     * 본 테스트는 "/" 요청 시, "Hello World!"가 보여지는지 검증하고 있어서 disabled 처리했습니다.
     */
    @Disabled
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var container = new ServletContainer();
        final var processor = new Http11Processor(socket, container);

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
        final var container = new ServletContainer();
        // 테스트용 서블릿 등록
        container.addServlet("/*", new StaticResourceServlet());
        final Http11Processor processor = new Http11Processor(socket, container);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
