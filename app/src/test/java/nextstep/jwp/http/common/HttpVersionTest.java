package nextstep.jwp.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotAllowedHttpVersionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @DisplayName("문자열을 이용한 Http 버전 조회시")
    @Nested
    class findByExtension {

        @DisplayName("성공하면 HttpVersion을 반환한다.")
        @Test
        void Success() {
            assertThat(HttpVersion.matchOf("HTTP/1.1")).isEqualTo(HttpVersion.HTTP_1_1);
        }

        @DisplayName("실패하면 예외가 발생한다.")
        @Test
        void Exception() {
            assertThatThrownBy(() -> HttpVersion.matchOf("wow"))
                .isExactlyInstanceOf(NotAllowedHttpVersionException.class);
        }
    }
}