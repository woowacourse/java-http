package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void 입력_문자열이_blank면_예외를_던진다() {
        // given
        final var input = " ";

        // when & then
        assertThatThrownBy(() -> Request.from(input))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Http Request가 비어있습니다.");
    }
}
