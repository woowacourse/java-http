package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청을_읽고_RequestLine과_RequestHeader_RequestBody로_분리할_수_있다() throws IOException {
        // given
        String requsetMessage = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "\r\n"
                + "body=requestBody";

        InputStream inputStream = new ByteArrayInputStream(requsetMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        inputStream.close();
        bufferedReader.close();

        // then
        assertThat(httpRequest).extracting("requestLine", "headers", "requestBody")
                .containsExactly(RequestLine.of("GET /index.html HTTP/1.1"),
                        Headers.of(List.of("Host: localhost:8080", "Connection: keep-alive")),
                        RequestBody.of(List.of("body=requestBody")));
    }

    @Test
    void 잘못된_요청을_받으면_예외를_던진다() {
        //given
        String invalidRequestMessage = "GET /index.html HTTP/1.1" + "Host: localhost:8080" + "Connection: keep-alive";

        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(bufferedReader))
                .isInstanceOf(Exception.class);
    }
}
