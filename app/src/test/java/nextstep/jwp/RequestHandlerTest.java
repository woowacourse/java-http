package nextstep.jwp;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static nextstep.jwp.model.http.HTTPMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        String result = socket.output();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                GET + " /index.html HTTP/1.1 ",
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
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5352 \r\n" +
                "\r\n" +
                body;
        assertThat(socket.output()).isEqualTo(expected);
    }
}
