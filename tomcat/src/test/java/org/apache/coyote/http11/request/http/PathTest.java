package org.apache.coyote.http11.request.http;

import org.apache.coyote.http11.request.line.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    @Nested
    class Path를_검증한다 {
        @Test
        void URI가_유효하면_생성한다() {
            // given
            final String path = "/woowacourse/level4/tomcat/bebe";

            // when, then
            assertDoesNotThrow(() -> Path.from(path));
        }

        @Test
        void 쿼리스트링이_요청되지_않으면_기본_Path만_생성된다() {
            // given
            final String request = "/woowacourse/level4/tomcat";

            Path path = Path.from(request);

            // when, then
            assertAll(
                    () -> assertThat(path.queryString())
                            .isEqualTo(null),
                    () -> assertThat(path.defaultPath())
                            .isEqualTo("/woowacourse/level4/tomcat")
            );
        }

        @Test
        void 쿼리스트링이_요청되면_쿼리스트링과_같이_생성된다() {
            // given
            final String request = "/woowacourse/level4/tomcat?name=베베";

            Path path = Path.from(request);

            // when, then
            assertAll(
                    () -> assertThat(path.queryString().get("name"))
                            .isEqualTo("베베"),
                    () -> assertThat(path.defaultPath())
                            .isEqualTo("/woowacourse/level4/tomcat")
            );
        }

        @Test
        void URI가_유효하지_않으면_예외가_발생한다() {
            // given
            final String path = "/woowacourse/level4/tomcat/**";

            // when, then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> Path.from(path)
            );
        }

    }

}
