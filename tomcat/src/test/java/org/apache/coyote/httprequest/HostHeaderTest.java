package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidHostHeaderException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class HostHeaderTest {

    @Test
    void 호스트_헤더에_DELIMITER_가_없으면_예외가_발생한다() {
        // given
        final String invalidHostHeader = "BEBE";

        // when, then
        assertThatThrownBy(() -> HostHeader.from(invalidHostHeader))
                .isInstanceOf(InvalidHostHeaderException.class);
    }

    @Test
    void 호스트_헤더의_포트가_숫자가_아니면_예외가_발생한다() {
        // given
        final String invalidHostHeader = "localhost:BEBE";

        // when, then
        assertThatThrownBy(() -> HostHeader.from(invalidHostHeader))
                .isInstanceOf(InvalidHostHeaderException.class);
    }
}
