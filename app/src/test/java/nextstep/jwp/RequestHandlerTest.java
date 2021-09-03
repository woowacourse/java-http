package nextstep.jwp;

import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.context.RequestHandler;
import nextstep.jwp.framework.util.ResourceUtils;

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
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
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
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                ResourceUtils.readString("/index.html"));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void error() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /error.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                ResourceUtils.readString("/404.html"));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void serverError() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n", "HTTP/1.1 500 Internal Server Error",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: 2357 ",
                "",
                ResourceUtils.readString("/500.html"));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
