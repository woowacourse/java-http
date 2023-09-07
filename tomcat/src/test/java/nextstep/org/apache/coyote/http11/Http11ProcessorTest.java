package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void 루트경로로_요청을_보내면_index_페이지를_응답한다() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceFile = resource.getFile();
        final Path path = Paths.get(resourceFile);
        final List<String> fileLines = Files.readAllLines(path);

        final String responseBody = String.join(System.lineSeparator(), fileLines);

        final var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "Content-Type: text/html ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/html",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceFile = resource.getFile();
        final Path path = Paths.get(resourceFile);
        final List<String> fileLines = Files.readAllLines(path);

        final String responseBody = String.join(System.lineSeparator(), fileLines);

        final var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "Content-Type: text/html ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
