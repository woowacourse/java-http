package nextstep.jwp;

import nextstep.jwp.http.RequestHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

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
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }
}
