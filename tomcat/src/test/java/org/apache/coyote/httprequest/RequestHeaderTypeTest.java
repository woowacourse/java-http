package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidRequestHeaderNameException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeaderTypeTest {

    @Test
    void 잘못된_이름의_헤더로_요청을_보내면_예외가_발생한다() {
        // given
        final String invalidHeaderName = "BEBE";

        // when, then
        assertThatThrownBy(() -> RequestHeaderType.from(invalidHeaderName))
                .isInstanceOf(InvalidRequestHeaderNameException.class);
    }
}
