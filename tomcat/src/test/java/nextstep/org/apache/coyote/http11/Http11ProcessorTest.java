package nextstep.org.apache.coyote.http11;

import nextstep.jwp.exception.NotFoundUserException;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void css() throws IOException {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/css;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void javascript() throws IOException {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /js/scripts.js HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: application/javascript;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void NotFoundFileException() {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /index.rookie HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: */*;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        assertThatThrownBy(() -> processor.process(socket))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void login() throws IOException {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/html;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");


        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void failed_login_when_not_have_id() {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /login?account=rookie&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/html;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");


        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        assertThatThrownBy(() -> processor.process(socket))
            .isInstanceOf(NotFoundUserException.class);
    }

    @Test
    void failed_login_when_wrong_password() {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /login?account=gugu&password=wrong HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/html;q=0.1 ",
            "Connection: keep-alive",
            "",
            "");


        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        assertThatThrownBy(() -> processor.process(socket))
            .isInstanceOf(NotFoundUserException.class);
    }
}
