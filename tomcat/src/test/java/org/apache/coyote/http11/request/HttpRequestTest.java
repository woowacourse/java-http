package org.apache.coyote.http11.request;

import org.apache.coyote.http11.InputReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("요청된 값의 uri를 알 수 있다.")
    void getUri() throws IOException {
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);
        HttpRequest httpRequest = new HttpRequest(inputReader);

        String uri = httpRequest.getUri();

        assertThat(uri).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("요청된 uri의 경로를 알 수 있다.")
    void getPath() throws IOException {
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);
        HttpRequest httpRequest = new HttpRequest(inputReader);

        String path = httpRequest.getPath();

        assertThat(path).isEqualTo("/login");
    }
}
