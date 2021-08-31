package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK" + System.lineSeparator() +
            "Content-Length: 4406 " + System.lineSeparator() +
            "Content-Type: text/html;charset=utf-8 " + System.lineSeparator() +
            System.lineSeparator() +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
