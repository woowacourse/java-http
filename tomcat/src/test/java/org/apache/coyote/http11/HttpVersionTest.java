package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.exception.NoSuchHttpVersionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @DisplayName("HttpVersion으로 변경한다.")
    @Test
    void HttpVersion으로_변경한다() {
        // given
        String httpVersion = "HTTP/1.1";

        // when
        HttpVersion actual = HttpVersion.from(httpVersion);

        // then
        assertThat(actual).isEqualTo(HttpVersion.HTTP11);
    }
    
    @DisplayName("httpVersion이 존재하지 않을 경우 예외를 던진다.")
    @Test
    void httpVersion이_존재하지_않을_경우_예외를_던진다() {
        // given
        String httpVersion = "HTTP/2.0";

        // when & then
        assertThatThrownBy(() -> HttpVersion.from(httpVersion))
                .isInstanceOf(NoSuchHttpVersionException.class);

    }
}
