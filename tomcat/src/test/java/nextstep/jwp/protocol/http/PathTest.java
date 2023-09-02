package nextstep.jwp.protocol.http;

import nextstep.jwp.protocol.request_line.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        void 쿼리스트링이_같이_요청되어도_생성된다() {
            // given
            final String path = "/woowacourse/level4/tomcat?name=베베";

            // when, then
            assertDoesNotThrow(() -> Path.from(path));
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
