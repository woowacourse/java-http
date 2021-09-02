package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestBody;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestBodyTest {
    @Test
    @DisplayName("POST 요청 - 본문(Body)이 존재하는 경우")
    void createRequestBody() throws IOException {
        final String body = "account=gugu&password=password&email=hkkang%40woowahan.com\r\n";
        final MockSocket socket = new MockSocket(body);
        RequestBody requestBody = new RequestBody(new BufferedReader(new InputStreamReader(socket.getInputStream())), 58);
        assertEquals("gugu", requestBody.getParam("account"));
        assertEquals("password", requestBody.getParam("password"));
    }

    @Test
    @DisplayName("POST 요청 - 본문(Body)이 존재하지 않는 경우")
    void createRequestBodyFailure() throws IOException {
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "");

        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        assertThrows(IllegalArgumentException.class, () -> httpRequest.getRequestBodyParam("invalid"));
    }
}
