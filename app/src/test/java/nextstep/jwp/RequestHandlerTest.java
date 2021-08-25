package nextstep.jwp;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler =
            new RequestHandler(socket);

        // when
        requestHandler.run();

        assertThat(socket.output()).contains(
            "HTTP/1.1 200 OK",
            "Content-Length: 12",
            "Content-Type: text/plain",
            "Hello world!"
        );
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler =
            new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html ",
            "대시보드"
        );
    }
}
