package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청을_읽고_RequestLine과_RequestHeader_RequestBody로_분리할_수_있다() throws IOException {
        // given
        String body = "body=requestBody";

        String requestMessage = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        inputStream.close();
        bufferedReader.close();

        // then
        assertThat(httpRequest).extracting("requestLine", "headers", "requestBody")
                .containsExactly(RequestLine.of("GET /index.html HTTP/1.1"),
                        Headers.of(List.of("Host: localhost:8080", "Connection: keep-alive",
                                "Content-Length: " + body.getBytes().length)),
                        new RequestBody(body));
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

    @Test
    void 요청의_method와_uri가_일치하는_지_확인한다() throws IOException {
        // given
        String body = "body=requestBody";

        String requestMessage = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        // when
        boolean result = httpRequest.matchRequestLine(HttpMethod.GET, Pattern.compile("/index.html"));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 요청의_method와_uri가_일치하지_않는_지_확인한다() throws IOException {
        // given
        String body = "body=requestBody";

        String requestMessage = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "\r\n"
                + body;

        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        // when
        boolean result = httpRequest.matchRequestLine(HttpMethod.POST, Pattern.compile("/index.html"));

        // then
        assertThat(result).isFalse();
    }
}
