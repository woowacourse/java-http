package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.httpmessage.Headers;
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청을_읽고_RequestLine과_RequestHeader_RequestBody로_분리할_수_있다() throws IOException {
        // given
        String body = "body=requestBody";

        String requestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );

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
    void 잘못된_요청을_받으면_예외를_던진다() throws IOException {
        //given
        String invalidRequestMessage = "GET/index.htmlHTTP/1.1Host:localhost:8080Connection:keep-alive";

        InputStream inputStream = new ByteArrayInputStream(invalidRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(bufferedReader))
                .isInstanceOf(Exception.class);

        inputStream.close();
        bufferedReader.close();
    }

    @Test
    void 요청_헤더에_쿠키가_존재할_때_쿠키를_추출할_수_있다() throws IOException {
        // given
        String requestMessage = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1 ",
                "Cookie: name=park",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 0 ",
                "",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        // when
        Optional<Cookie> cookie = httpRequest.getCookie();

        // then
        assertAll(
                () -> assertThat(cookie).isPresent(),
                () -> assertThat(cookie.get()).extracting("cookies")
                        .isEqualTo(Map.of("name", "park"))
        );
    }

    @Test
    void 요청_헤더에_쿠키가_존재하지_않을_때_null을_반환한다() throws IOException {
        // given
        String requestMessage = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 0 ",
                "",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.of(bufferedReader);

        // when
        Optional<Cookie> cookie = httpRequest.getCookie();

        // then
        assertThat(cookie).isEmpty();
    }
}
