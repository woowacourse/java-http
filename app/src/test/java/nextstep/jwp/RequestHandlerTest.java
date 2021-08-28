package nextstep.jwp;

import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.RequestHandler;

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
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5670"
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
        BeanFactory.init();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5670"
        );
    }
}
