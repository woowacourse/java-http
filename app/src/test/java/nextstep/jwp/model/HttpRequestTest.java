package nextstep.jwp.model;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.model.httpMessage.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpMessage.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void request_GET_INDEXT() throws IOException {
        String value = String.join("\r\n",
                GET + " /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        MockSocket socket = new MockSocket(value);
        RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.run();

        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getHeaders().size()).isNotNegative();
    }

    @Test
    void request_GET_LOGIN() throws IOException {
        String value = String.join("\r\n",
                GET + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(value);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getHeaders().size()).isNotNegative();
    }

    @Test
    void request_CSS() throws IOException {
        String value = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css ",
                "",
                "Hello world!");
        final MockSocket socket = new MockSocket(value);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        socket.close();

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/css/styles.css");
        assertThat(request.getHeaders().size()).isNotNegative();
    }
}