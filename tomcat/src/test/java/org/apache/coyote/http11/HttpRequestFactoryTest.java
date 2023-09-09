package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestFactoryTest {

    @DisplayName("HTTP 요청을 생성한다.")
    @Test
    void createHttpRequest() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            "POST /login HTTP/1.1\r\n"
                + "Content-Length: 30\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "\r\n"
                + "account=gugu&password=password"
        ))) {
            // when
            final HttpRequest httpRequest = HttpRequestFactory.createHttpRequest(bufferedReader);

            // then
            assertAll(
                () -> assertThat(httpRequest.getBody().containsKey("account")).isTrue(),
                () -> assertThat(httpRequest.getBody().containsKey("password")).isTrue(),
                () -> assertThat(httpRequest.getHeaders().getHeaderValue(HttpHeaderName.CONTENT_LENGTH))
                    .isEqualTo("30"),
                () -> assertThat(httpRequest.getHeaders().getHeaderValue(HttpHeaderName.CONTENT_TYPE))
                    .isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(httpRequest.getRequestURI().getPath()).isEqualTo("/login")
            );
        }
    }

    @DisplayName("body 가 없으면 빈 Map 을 저장한다.")
    @Test
    void createFromEmptyMap() throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            "GET /index.html HTTP/1.1\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n"
        ))) {
            // when
            final HttpRequest httpRequest = HttpRequestFactory.createHttpRequest(bufferedReader);

            // then
            assertAll(
                () -> assertThat(httpRequest.getBody().isEmpty()).isTrue(),
                () -> assertThat(httpRequest.getHeaders().getHeaderValue(HttpHeaderName.CONTENT_LENGTH))
                    .isEqualTo("0"),
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getRequestURI().getPath()).isEqualTo("/index.html")
            );
        }
    }
}
