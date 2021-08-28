package nextstep.jwp.httpserver.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HttpVersion 단위 테스트")
class HttpVersionTest {

    @Test
    @DisplayName("version 검색")
    void version() {
        // given
        String version = "HTTP/1.1";

        // when
        HttpVersion httpVersion = HttpVersion.version(version);

        // then
        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("없는 version 검색")
    void notExistVersion() {
        // given
        String version = "HTTP/1.0";

        // when
        // then
        assertThatThrownBy(() -> HttpVersion.version(version))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
