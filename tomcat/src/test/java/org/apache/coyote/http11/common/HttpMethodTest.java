package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.InvalidHttpMethodException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void 올바르지_않은_HttpMethod인_경우_InvalidHttpMethodException_예외를_던진다() {
        // given
        final String http = "GET2";

        // expect
        assertThatThrownBy(() -> HttpMethod.from(http))
                .isInstanceOf(InvalidHttpMethodException.class)
                .hasMessage("올바르지 않은 HttpMethod 형식입니다.");
    }
}
