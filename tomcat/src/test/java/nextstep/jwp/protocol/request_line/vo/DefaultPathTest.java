package nextstep.jwp.protocol.request_line.vo;

import org.apache.coyote.http11.request.line.vo.DefaultPath;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultPathTest {

    @Nested
    class DefaultPath_생성을_검증한다 {

        @Test
        void 유효한_기본_path라면_생성한다() {
            // given
            final String value = "/woowacourse/level4/tomcat";

            // when
            DefaultPath defaultPath = DefaultPath.from(value);

            // then
            assertThat(defaultPath.value()).isEqualTo(value);
        }

        @Test
        void 유효하지_않은_path라면_예외가_발생한다() {
            // given
            final String value = "//woowacourse/level4&&**";

            // when, then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> DefaultPath.from(value)
            );
        }

    }

}
