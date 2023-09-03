package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidConnectionHeaderException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class ConnectionHeaderTest {

    @Test
    void Connection_헤더가_잘못된_경우_예외가_발생한다() {
        // given
        final String invalidConnectionHeader = "my-love-bebe";

        // when, then
        assertThatThrownBy(() -> ConnectionHeader.from(invalidConnectionHeader))
                .isInstanceOf(InvalidConnectionHeaderException.class);
    }
}
