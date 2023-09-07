package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javassist.NotFoundException;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.util.ResourceReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("소켓으로 들어온 요청을 처리한다.")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "Hello world!"
        );
    }

    @DisplayName("소켓으로 들어온 /index.html 요청을 처리한다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("로그인 성공 시 응답 헤더에 JSESSIONID 값을 반환하고 index.html로 리다이렉트한다.")
    @Test
    void loginPost() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 FOUND",
                "Content-Type: text/html;charset=utf-8",
                "Location: /index.html",
                "Set-Cookie:"
        );
    }

    @DisplayName("/login GET 요청 시 login.html을 반환한다.")
    @Test
    void loginGet() throws IOException, NotFoundException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "" + System.lineSeparator()
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                ResourceReader.read("/login.html").getContentBytes()
        );

    }

    @DisplayName("/register GET 요청 시 login.html을 반환한다.")
    @Test
    void registerGet() throws IOException, NotFoundException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "" + System.lineSeparator()
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                ResourceReader.read("/register.html").getContentBytes()
        );
    }

}
