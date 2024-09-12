package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("문자열을 파싱하여 요청 라인을 생성한다.")
    @Test
    void creatRequestLine() {
        String rawRequestLine = "GET /1234 HTTP/1.1";

        RequestLine requestLine = RequestLine.of(rawRequestLine);

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/1234"),
                () -> assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1")
        );
    }
}
