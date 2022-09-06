package nextstep.org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("http 요청 시 css 파일을 요청할 수 있다.")
    void css() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive ",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");

        var expected= String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/css;charset=utf-8 ",
            "Content-Length: 211991 ",
            "",
            new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인에 성공하면 302 상태코드를 반환하고 index.html로 이동한다.")
    void login_success() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
            "POST /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/html,*/*;q=0.1",
            "Connection: keep-alive ",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        var expected= String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 5564 ",
            "",
            new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인에 실패하면 401 상태코드를 반환하고 401.html로 이동한다.")
    void login_fail() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
            "POST /login?account=gugu&password=wrong HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/html,*/*;q=0.1",
            "Connection: keep-alive ",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");

        var expected= String.join("\r\n",
            "HTTP/1.1 401 Unauthorized ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 2426 ",
            "",
            new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
