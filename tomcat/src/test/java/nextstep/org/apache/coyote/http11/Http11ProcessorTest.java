package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("/에 요청을 보내면 hello world!가 응답된다.")
    void home() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 12",
                "Content-Type: text/html;charset=utf-8",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("index.html 파일을 요청하면 index.html 파일이 전달된다.")
    void index() throws IOException, URISyntaxException {
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

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 5564\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(Path.of(resource.toURI())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 파일을 요청하면 content-type은 text/css로 해서 css 파일을 전달한다. ")
    void css() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: text/css",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expectedContentType = "text/css";

        assertThat(socket.output()).contains(expectedContentType);
    }
}
