package nextstep.jwp;

import nextstep.jwp.webserver.RequestHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 Ok",
                "Content-Length: " + expectedBody.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "",
                expectedBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html" //http://localhost
        );
        assertThat(socket.output()).isEqualTo(expected);
    }
}
