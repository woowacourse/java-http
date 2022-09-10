package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.util.HttpMethod;
import org.junit.jupiter.api.Test;

class RequestLineTest {
    @Test
    void RequestLine으로_HttpMethod_RequestUri를_생성() {
        // given, when
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1 ");

        // then
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getRequestUri()).isEqualTo(RequestUri.from("/index.html"))
        );
    }
}