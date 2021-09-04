package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.framework.RequestHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("GET 요청")
    @Test
    void get() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);
        final Path path = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("static/404.html")).getPath()).toPath();
        final String htmlValue = Files.readString(path);

        // when
        requestHandler.run();
        String content = socket.output();

        // then
        assertThat(content).contains(
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 2426",
            htmlValue
        );
    }

    @DisplayName("POST 요청")
    @Test
    void post() throws IOException {
        // given
        final MockSocketWithBody socket = new MockSocketWithBody();
        final RequestHandler requestHandler = new RequestHandler(socket);
        final Path path = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("static/index.html")).getPath()).toPath();
        final String htmlValue = Files.readString(path);

        // when
        requestHandler.run();
        String content = socket.output();

        // then
        assertThat(content).contains(
            "HTTP/1.1 302 Found ",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 5518",
            htmlValue
        );
    }

    @Disabled
    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 5564 \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
