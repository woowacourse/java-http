package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("request header 정상 저장")
    @Test
    void getHeaders() throws IOException {
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080 ");
        headers.put("Connection", "keep-alive ");

        try (final StringReader stringReader = new StringReader(request);
             final BufferedReader bufferedReader = new BufferedReader(stringReader)) {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            assertThat(httpRequest.getHeader()).isEqualTo(headers);
        }
    }


    @DisplayName("request body의 값들을 정상 저장")
    @Test
    void getRequestBody() throws IOException {
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "Connection: keep-alive ",
                        "Content-Length: 58 ",
                        "Content-Type: application/x-www-form-urlencoded ",
                        "Accept: */* ",
                        "",
                        "account=gugu&password=password&email=hkkang%40woowahan.com");
        final Map<String, String> bodyValues = new HashMap<>();
        bodyValues.put("account", "gugu");
        bodyValues.put("password", "password");
        bodyValues.put("email", "hkkang%40woowahan.com");

        try (final StringReader stringReader = new StringReader(request);
             final BufferedReader bufferedReader = new BufferedReader(stringReader)) {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            assertThat(httpRequest.getBody()).isEqualTo(bodyValues);
        }
    }
}
