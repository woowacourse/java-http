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
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);
        final Path path = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("404.html")).getPath()).toPath();
        final String htmlValue = Files.readString(path);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Length: 12 ",
            "Content-Type: text/html;charset=utf-8 ",
            "",
            htmlValue);
        assertThat(socket.output()).isEqualTo(expected);
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
