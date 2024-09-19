package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.constants.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("요청의 첫줄로 RequestLine을 만든다")
    @Test
    void generateRequestLine() {
        String request = "GET /test HTTP/1.1";

        final RequestLine requestLine = RequestLine.from(request);

        assertAll(
                () -> Assertions.assertThat(requestLine.method()).isEqualTo(HttpMethod.GET),
                () -> Assertions.assertThat(requestLine.uri()).isEqualTo("/test"),
                () -> Assertions.assertThat(requestLine.version()).isEqualTo("HTTP/1.1")
        );
    }

    @DisplayName("요청의 uri가 없으면 /index.html을 기본으로 설정한다.")
    @Test
    void setDefaultPage() {
        String request = "GET  HTTP/1.1";
        final RequestLine requestLine = RequestLine.from(request);

        Assertions.assertThat(requestLine.uri()).isEqualTo("/index.html");
    }
}
