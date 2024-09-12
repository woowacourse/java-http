package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.exception.HttpFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("올바르지 않은 요청 라인 형식인 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET /", "", "   ", "/1234 HTTP/1.1"})
    void invalidRequestLine(String rawRequestLine) {
        assertThatThrownBy(() -> RequestLine.of(rawRequestLine))
                .isInstanceOf(HttpFormatException.class)
                .hasMessage("올바르지 않은 request line 형식입니다.");
    }
}
