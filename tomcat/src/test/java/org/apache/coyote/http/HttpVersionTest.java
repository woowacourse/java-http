package org.apache.coyote.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @Test
    @DisplayName("잘못된 버전을 찾는 경우 예외를 발생한다.")
    void validateVersion() {
        String test = "HTP/1.0";

        assertThatThrownBy(() -> HttpVersion.findVersionByProtocolVersion(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown http version: HTP/1.0");
    }

}
