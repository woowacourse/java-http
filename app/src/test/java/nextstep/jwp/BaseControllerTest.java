package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BaseControllerTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void run() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(output).isEqualTo(expected);
    }

    private String runRequestHandler(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, requestMapping);
        requestHandler.run();
        return socket.output();
    }
}
