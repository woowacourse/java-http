package nextstep.jwp;

import nextstep.jwp.controller.Controllers;
import nextstep.jwp.server.RequestHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final Controllers controllers = Controllers.loadContext();
        final RequestHandler requestHandler = new RequestHandler(socket, controllers);

        // when
        requestHandler.run();

        // then
        String expected = "\n\n"
            + "HTTP/1.1 200 OK \n"
            + "Content-Length: 11 \n"
            + "Content-Type: text/html; charset=UTF-8 \n"
            + "\n"
            + "hihi hello!";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final Controllers controllers = Controllers.loadContext();
        final RequestHandler requestHandler = new RequestHandler(socket, controllers);

        // when
        requestHandler.run();

        // then
        String expected = "\n\n"
            + "HTTP/1.1 200 OK \n"
            + "Content-Length: 11 \n"
            + "Content-Type: text/html; charset=UTF-8 \n"
            + "\n"
            + "hihi hello!";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
