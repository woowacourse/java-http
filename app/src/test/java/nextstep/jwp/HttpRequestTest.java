package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.request.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpRequestTest {
    @Test
    @DisplayName("HttpRequest 생성 - GET")
    void createGetHttpRequest() throws IOException {
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 0",
                "",
                "");

        final MockSocket socket = new MockSocket(request);
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        assertTrue(httpRequest.isGet());
    }

    @Test
    @DisplayName("HttpRequest 생성 - POST")
    void createPostHttpRequest() throws IOException {
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account = gugu&password=password&email=hkkang%40woowahan.com"
        );

        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        assertTrue(httpRequest.isPost());
    }
}
