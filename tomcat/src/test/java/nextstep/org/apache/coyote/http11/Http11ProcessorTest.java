package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.handler.HandlerMapper;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var handlerMapper = new HandlerMapper();
        final var processor = new Http11Processor(socket, handlerMapper);

        // when
        processor.process(socket);

        // then
        final List<String> expectedLines = List.of(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 12 \r\n",
                "\r\nHello world!"
        );

        assertThat(socket.output()).contains(expectedLines);
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

        final var handlerMapper = new HandlerMapper();
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, handlerMapper);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final List<String> expectedLines = List.of(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 5564 \r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
        assertThat(socket.output()).contains(expectedLines);
    }
}
