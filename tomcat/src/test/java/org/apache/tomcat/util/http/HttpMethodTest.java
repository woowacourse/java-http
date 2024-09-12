package org.apache.tomcat.util.http;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("적합한 Http Method를 찾지 못한경우 예외가 발생한다.")
    @Test
    void of() {
        assertThatThrownBy(() -> HttpMethod.of("HEAD"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("요청하신 HTTP METHOD는 지원하지 않습니다.");
    }
}
