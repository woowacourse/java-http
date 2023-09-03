package org.apache.coyote.http11.request.line;

import org.apache.coyote.http11.request.line.HttpMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpMethodTest {

    @Nested
    class HTTP_METHOD_검증 {

        @Test
        void 유효한_HTTP_METHOD면_생성한다() {
            // when, then
            for (HttpMethod httpMethod : HttpMethod.values()) {
                assertDoesNotThrow(
                        () -> HttpMethod.from(httpMethod.name())
                );
            }
        }

        @Test
        void 유효하지_않은_HTTP_METHOD면_예외가_발생한다() {
            // given
            final String httpMethod = "POSTS";

            // when, then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> HttpMethod.from(httpMethod)
            );
        }

    }

}
