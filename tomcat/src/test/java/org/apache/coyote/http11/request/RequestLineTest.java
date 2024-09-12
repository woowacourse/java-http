package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("String 한 줄로 RequestLine 객체를 생성할 수 있다.")
    @Test
    void createRequestLine() {
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

    @DisplayName("request line을 공백 기준으로 쪼갰을 때, 3개의 요소로 이루어져있지 않으면 예외를 발생시킨다.")
    @Test
    void createRequestFail() {
        // given
        String rawRequestLine = "GET /loginHTTP/1.1";

        // when & then
        assertThatThrownBy(() -> new RequestLine(rawRequestLine)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("request line은 공백 기준으로 쪼갰을 때, 3개의 요소로 이루어져야 합니다. " +
                        "현재 요소 갯수 = 2");
    }
}
