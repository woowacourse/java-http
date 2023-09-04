package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
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
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "Set-Cookie:",
                "Hello world!"
        );
    }

    @DisplayName("소켓으로 들어온 /index.html 요청을 처리한다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=4e9341e9-fb1f-4630-b8ff-803e9cc26404 ",
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
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("요청 헤더의 Cookie에 JSESSIONID 값이 존재하지 않으면 추가한다.")
    @Test
    void checkSessionId() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "Set-Cookie:",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}
