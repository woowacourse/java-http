package nextstep.jwp;

import nextstep.jwp.server.handler.RequestHandler;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class
RequestHandlerTest {

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

    @Test
    void post() {
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                "account=gugu&password=password");


        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler =
                new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "302"
        );
    }
}
