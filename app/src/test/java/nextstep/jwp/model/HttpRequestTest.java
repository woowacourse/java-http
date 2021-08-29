package nextstep.jwp.model;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.model.httpMessage.HttpProtocol;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpMessage.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void request_GET_INDEXT() throws IOException {
        String value = String.join("\r\n",
                GET + " /index.html " + HttpProtocol.NAME + " ",
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
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void request_GET_LOGIN() throws IOException {
        String value = String.join("\r\n",
                GET + " /login " + HttpProtocol.NAME + " ",
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
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void request_GET_LOGIN_WITH_PARAMS() throws IOException {
        String value = String.join("\r\n",
                GET + " /login " + HttpProtocol.NAME + " ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password");
        final MockSocket socket = new MockSocket(value);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void request_CSS() throws IOException {
        String value = String.join("\r\n",
                GET + " /css/styles.css " + HttpProtocol.NAME + " ",
                "Host: localhost:8080 ",
                "Accept: text/css ",
                "");
        final MockSocket socket = new MockSocket(value);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        socket.close();

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/css/styles.css");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Accept")).isEqualTo("text/css");
    }
}