package nextstep.jwp.server;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.MockSocket;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestMapping requestMapping = RequestMapping.loadContext();
        final RequestHandler requestHandler = new RequestHandler(socket, requestMapping);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 200 OK \n"
            + "Content-Length: 11 \n"
            + "Content-Type: text/html; charset=UTF-8 \n"
            + "\n"
            + "hihi hello!";

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
        final RequestMapping requestMapping = RequestMapping.loadContext();
        final RequestHandler requestHandler = new RequestHandler(socket, requestMapping);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 200 OK \n"
            + "Content-Length: 11 \n"
            + "Content-Type: text/html; charset=UTF-8 \n"
            + "\n"
            + "hihi hello!";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
