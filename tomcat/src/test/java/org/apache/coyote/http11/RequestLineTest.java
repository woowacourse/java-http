package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parse() {
        // given
        final String requestLineString = "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = new RequestLine(requestLineString);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void parseWithQueryParams() {
        // given
        final String requestLineString = "GET /login?account=gugu&password=password HTTP/1.1";
        final String account = "account";
        final String password = "password";
        final String none = "none";
        final String expectedAccount = "gugu";
        final String expectedPassword = "password";
        final String expectedNone = "";

        // when
        final RequestLine requestLine = new RequestLine(requestLineString);

        // then
        assertAll(
                () -> assertThat(requestLine.getPath()).isEqualTo("/login"),
                () -> assertThat(requestLine.getQueryParam(account)).isEqualTo(expectedAccount),
                () -> assertThat(requestLine.getQueryParam(password)).isEqualTo(expectedPassword),
                () -> assertThat(requestLine.getQueryParam(none)).isEqualTo(expectedNone)
        );
    }
}
