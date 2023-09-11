package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
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
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                ""
        );

        final var socket = new StubSocket(httpRequest);

        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 지원하지_않는_요청_시_404_페이지를_반환한다() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /Index" + Integer.MAX_VALUE + "HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                ""
        );

        final var socket = new StubSocket(httpRequest);

        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 2426",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).isEqualTo(expected);
    }
}
