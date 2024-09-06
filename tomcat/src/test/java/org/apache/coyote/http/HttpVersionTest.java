package org.apache.coyote.http;

import org.apache.coyote.http.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpVersionTest {

    @Test
    @DisplayName("잘못된 버전을 찾는 경우 예외를 발생한다.")
    void validateVersion() {
        String test = "HTP/1.0";

        assertThatThrownBy(() -> HttpVersion.findByVersion(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown http version: HTP/1.0");
    }

}
