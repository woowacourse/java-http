package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidHttpMethodException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class RequestMethodTest {

    @Test
    void 잘못된_메소드로_요청하면_예외가_발생한다() {
        // given
        final String invalidMethodName = "BEBE";

        //when, then
        assertThatThrownBy(() -> RequestMethod.from(invalidMethodName))
                .isInstanceOf(InvalidHttpMethodException.class);
    }
}
