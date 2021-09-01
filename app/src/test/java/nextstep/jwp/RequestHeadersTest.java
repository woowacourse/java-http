package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.request.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestHeadersTest {
    @DisplayName("RequestHeaders 생성")
    @Test
    void create() throws IOException {

        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "");

        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())));

        assertEquals("localhost:8080", httpRequest.get("Host"));
        assertEquals("keep-alive", httpRequest.get("Connection"));
        assertEquals("30", httpRequest.get("Content-Length"));
    }
}
