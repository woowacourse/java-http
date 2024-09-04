package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("Http 메서드가 올바르지 않은 경우 파싱에 실패한다.")
    void invalidHttpMethodTest() {
        assertThatThrownBy(() -> HttpMethod.from("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HTTP method: INVALID");
    }
}
