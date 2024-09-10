package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("문자열로부터 Request-Line을 생성한다.")
    void createRequestLine() {
        String line = "GET /index.html?query=abc&woowa=course HTTP/1.1";
        assertDoesNotThrow(() -> new RequestLine(line));
    }

    @Test
    @DisplayName("토큰의 개수가 3개가 아닌 경우, 예외가 발생한다.")
    void invalidTokenLength() {
        String line = "GET /index.html?query=abc";
        assertThatThrownBy(() -> new RequestLine(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request-Line should have 3 tokens");
    }
}
