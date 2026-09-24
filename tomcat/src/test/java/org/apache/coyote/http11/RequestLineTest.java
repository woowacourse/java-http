package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RequestLineTest {

    @Test
    void getMethod_success() {
        // given
        final String httpRequest = "GET /index.html HTTP/1.1";
        RequestLine requestLine = new RequestLine(httpRequest);

        // when
        String result = requestLine.getMethod();

        // then
        assertThat(result).isEqualTo("GET");
    }

    @Test
    void getUri_success() {
        // given
        final String httpRequest = "GET /index.html HTTP/1.1";
        RequestLine requestLine = new RequestLine(httpRequest);

        // when
        String result = requestLine.getUri();

        // then
        assertThat(result).isEqualTo("/index.html");
    }

    @Test
    void getVersion_success() {
        // given
        final String httpRequest = "GET /index.html HTTP/1.1";
        RequestLine requestLine = new RequestLine(httpRequest);

        // when
        String result = requestLine.getVersion();

        // then
        assertThat(result).isEqualTo("HTTP/1.1");
    }
}
