package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void request_get_요청_생성_테스트() throws IOException {
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest httpRequest = HttpRequest.of(byteArrayInputStream);
        assertAll(
                () -> assertThat(httpRequest.getPath()).isEqualTo("/index.html")
        );
    }

    @Test
    void request_post_요청_생성_테스트() throws IOException {
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        int contentLength = body.getBytes().length;
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: "+contentLength,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest httpRequest = HttpRequest.of(byteArrayInputStream);
        assertAll(
                () -> assertThat(httpRequest.getRequestBody()).isEqualTo(body),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/register")
        );
    }
}
