package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.InvalidHttpMethodException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void 올바르지_않은_메서드_값이_들어오면_예외를_반환한다() {
        // given
        String value = "invalid";

        // when, then
        assertThatThrownBy(() -> HttpMethod.from(value)).isExactlyInstanceOf(InvalidHttpMethodException.class);
    }
}
