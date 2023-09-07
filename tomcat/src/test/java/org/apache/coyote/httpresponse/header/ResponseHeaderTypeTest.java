package org.apache.coyote.httpresponse.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class ResponseHeaderTypeTest {

    @Test
    void 올바르지_않은_헤더_타입으로_생성하면_예외가_발생한다() {
        // given
        final String invalidHeaderName = "bebe";

        // when, then
        assertThatThrownBy(() -> ResponseHeaderType.from(invalidHeaderName))
                .isInstanceOf(RuntimeException.class);
    }
}
