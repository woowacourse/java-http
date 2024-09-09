package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("RequestLine을 파싱하는데 성공한다.")
    @Test
    void parseRequestLine() {
        // given
        String rawRequestLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine requestLine = new RequestLine(rawRequestLine);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getUrl()).isEqualTo("/index.html")
        );
    }
}