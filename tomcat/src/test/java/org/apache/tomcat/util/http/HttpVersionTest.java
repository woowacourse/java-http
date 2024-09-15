package org.apache.tomcat.util.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @DisplayName("적합한 Http Version을 찾지 못한경우 예외가 발생한다.")
    @Test
    void of() {
        assertThatThrownBy(() -> HttpVersion.of("HTTP/3.0"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("요청하신 HTTP Version은 지원하지 않습니다.");
    }
}
