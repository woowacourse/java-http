package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    @DisplayName("Request Line 의 인자가 세 개가 아닐 때 예외를 발생한다.")
    void validateSize() {
        String test = "GET /index.html HTTP/1.1 wrong";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid request line");
    }
}
