package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.http.HttpVersionNotMatchException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpVersionTest {

    @Nested
    class HTTP_버전_추출 {

        @Test
        void 존재하는_버전이라면_HTTP_버전을_반환한다() {
            final String version = "HTTP/1.1";

            final HttpVersion httpVersion = HttpVersion.from(version);

            assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_11);
        }

        @Test
        void 존재하지_않는_버전이라면_예외를_던진다() {
            final String version = "HTTP/5.0";

            assertThatThrownBy(() -> HttpVersion.from(version))
                    .isInstanceOf(HttpVersionNotMatchException.class)
                    .hasMessage("Http Version Not Matched");
        }
    }
}
