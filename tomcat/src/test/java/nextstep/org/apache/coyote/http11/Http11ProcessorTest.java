package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.SignUpRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.coyote.http11.Http11Processor;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final List<RequestHandler> requestHandlers = List.of(
            new RootPageRequestHandler(),
            new LoginRequestHandler(),
            new SignUpRequestHandler(),
            new StaticResourceRequestHandler()
    );

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(requestHandlers, socket);

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
        final Http11Processor processor = new Http11Processor(requestHandlers, socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(requestHandlers, socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: 211991 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 로그인_성공() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(requestHandlers, socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();
        assertThat(actual).contains("HTTP/1.1 302 FOUND \r\n");
        assertThat(actual).contains("Location: /index.html \r\n");
        assertThat(actual).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void 로그인_실패() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Length: 31",
                "",
                "account=gugu&password=password2");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(requestHandlers, socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html \r\n" +
                "\r\n";

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 회원가입_성공() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Content-Length: 55",
                "",
                "account=mallang&email=mallang%40naver.com&password=1234");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(requestHandlers, socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }
}
