package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void createHttpRequest() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        HttpRequest request = HttpRequest.from(bufferedReader);

        assertAll(
                () -> assertThat(request.getRequestLine().getMethod()).isEqualTo("GET"),
                () -> assertThat(request.getRequestLine().getPath()).isEqualTo("/index.html"),
                () -> assertThat(request.getRequestLine().getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(request.getRequestHeaders().getValue()).hasSize(2),
                () -> assertThat(request.getRequestBody().getValue()).isEqualTo("")
        );
    }
}
