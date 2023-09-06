package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpMethodTest {

    @Nested
    class from {

        @Test
        void 파라미터가_null이면_예외() {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원되지 않는 HTTP 메서드 입니다.");
        }

        @Test
        void 파라미터가_지원되지_않는_프로토콜이면_예외() {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from("HELLO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원되지 않는 HTTP 메서드 입니다.");
        }
    }
}
