package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundHttpVersionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @Test
    void findBy() {
        // given
        final String version = "HTTP/1.1";

        // when
        final HttpVersion actual = HttpVersion.findBy(version);

        // then
        assertThat(actual).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    void findByInvalidVersion() {
        // given
        final String version = "INVALID";

        // when & then
        assertThatThrownBy(() -> HttpVersion.findBy(version)).isInstanceOf(NotFoundHttpVersionException.class)
                                                             .hasMessage("해당 HTTP 버전을 찾을 수 없습니다.");
    }
}
