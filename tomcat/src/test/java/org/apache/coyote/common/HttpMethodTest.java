package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.http.HttpMethodNotMatchException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Nested
    class HTTP_메서드_추출 {

        @Test
        void 존재하는_메서드라면_HTTP_메서드를_반환한다() {
            final String method = "GET";

            final HttpMethod httpMethod = HttpMethod.from(method);

            assertThat(httpMethod).isEqualTo(HttpMethod.GET);
        }

        @Test
        void 존재하지_않는_메서드라면_예외를_던진다() {
            final String method = "HELLO";

            assertThatThrownBy(() -> HttpMethod.from(method))
                    .isInstanceOf(HttpMethodNotMatchException.class)
                    .hasMessage("Http Method Not Matched");
        }
    }
}
