package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.coyote.http11.message.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("Request Message가 담긴 BufferedReader 로부터 HttpRequest 를 생성한다.")
    void from() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final int contentLength = body.getBytes().length;
        final String httpRequestMessage = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + contentLength,
            "",
            body,
            "");

        // when
        HttpRequest httpRequest;
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // then
        assertAll(
            () -> assertThat(httpRequest.getRequestLine().getMethod()).isEqualTo(HttpMethod.GET),
            () -> assertThat(httpRequest.getRequestLine().getPath()).isEqualTo("/index.html"),
            () -> assertThat(httpRequest.getHeaders().getHeadersWithValue())
                .containsExactlyInAnyOrderEntriesOf(
                    Map.of(
                        "Host", "localhost:8080",
                        "Connection", "keep-alive",
                        "Content-Length", String.valueOf(contentLength)
                    )),
            () -> assertThat(httpRequest.getBodyOf("account")).isEqualTo("gugu"),
            () -> assertThat(httpRequest.getBodyOf("password")).isEqualTo("password")
        );
    }

    @Test
    @DisplayName("Content-Length 필드가 없다면 빈 body가 담긴다.")
    void from_contentLengthNotExist() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpRequest httpRequest;
        try (final InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // then
        assertAll(
            () -> assertThat(httpRequest.getRequestLine().getMethod()).isEqualTo(HttpMethod.GET),
            () -> assertThat(httpRequest.getRequestLine().getPath()).isEqualTo("/index.html"),
            () -> assertThat(httpRequest.getHeaders().getHeadersWithValue())
                .containsExactlyInAnyOrderEntriesOf(
                    Map.of(
                        "Host", "localhost:8080",
                        "Connection", "keep-alive"
                    )),
            () -> assertThat(httpRequest.getRequestBody()).isEmpty()
        );
    }
}
