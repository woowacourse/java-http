package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("String 한 줄로 RequestLine 객체를 생성할 수 있습니다.")
    @Test
    void tdd() {
        // given
        String rawRequestLine = "GET /login HTTP/1.1";

        // when
        RequestLine requestLine = new RequestLine(rawRequestLine);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getPath()).isEqualTo("/login"),
                () -> assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1")
        );
    }
}
