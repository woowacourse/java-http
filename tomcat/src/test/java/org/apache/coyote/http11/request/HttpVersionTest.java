package org.apache.coyote.http11.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {
    @DisplayName("지원하지 않는, 잘못된 형식의 Version인 경우 예외를 발생시킨다.")
    @Test
    void cannotFindVersion() {
        // when & then
        assertThatThrownBy(() -> HttpVersion.of("HTTP/0.9"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
