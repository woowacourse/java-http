package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @Test
    void 입력_문자열이_blank면_예외를_던진다() {
        // given
        final var input = " ";

        // when & then
        assertThatThrownBy(() -> RequestStartLine.from(input))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Http Request Start Line이 비어있습니다.");
    }

    @Test
    void 입력_문자열이_형식에_맞지_않을_경우_예외를_던진다() {
        // given
        final var input = "GET/index.html HTTP/1.1 ";

        // when & then
        assertThatThrownBy(() -> RequestStartLine.from(input))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("Http Request Start Line이 형식에 맞지 않습니다.");
    }
}
