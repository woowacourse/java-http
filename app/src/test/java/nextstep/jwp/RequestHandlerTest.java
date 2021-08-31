package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    @DisplayName("기본 화면으로 index.html을 보여준다.")
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("index.html을 보여준다.")
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
