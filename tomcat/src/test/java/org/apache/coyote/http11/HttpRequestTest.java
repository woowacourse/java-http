package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("http request 파싱 요청 바디 없을 때")
    void parseNoBody() throws IOException {
        //given
        final var stringReader = new StringReader(
                "GET / HTTP/1.1\r\nHost: localhost:8080 \r\nCookie: myCookie ");
        final var bufferedReader = new BufferedReader(stringReader);
        final var expectedHttpHeaders = Map.of("Host", "localhost:8080", "Cookie", "myCookie");

        //when
        final var request = HttpRequest.from(bufferedReader);

        //then
        assertThat(request.getRequestLine().getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeaders().getHeaders()).isEqualTo(expectedHttpHeaders);
    }

    @Test
    @DisplayName("http request 파싱 요청 바디 있을 때")
    void parseBody() throws IOException {
        //given
        final var stringReader = new StringReader("POST /login HTTP/1.1 \r\n" +
                                                  "Host: localhost:8080 \r\n" +
                                                  "Connection: keep-alive \r\n" +
                                                  "Content-Length: 30\r\n" +
                                                  "Content-Type: application/x-www.form-urlencoded \r\n" +
                                                  "Accept: */* \r\n" +
                                                  "\r\n" +
                                                  "account=redddy&password=486 \r\n" +
                                                  "");
        final var bufferedReader = new BufferedReader(stringReader);
        final var expected = Map.of("account", "redddy", "password", "486");

        //when
        final var request = HttpRequest.from(bufferedReader);

        //then
        assertThat(request.getBody()).isEqualTo(expected);
    }
}
