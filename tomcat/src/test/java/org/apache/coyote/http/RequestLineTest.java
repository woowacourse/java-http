package org.apache.coyote.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RequestLineTest {

    @Test
    @DisplayName("")
    void validateSize() {
        String test = "GET /index.html HTTP/1.1 wrong";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid request line");
    }
}
