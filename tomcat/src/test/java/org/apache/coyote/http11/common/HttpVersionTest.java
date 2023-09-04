package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.InvalidHttpVersionException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpVersionTest {

    @Test
    void Http의_version_이름으로_반환한다() {
        // given
        String version = "HTTP/1.1";

        // when
        HttpVersion httpVersion = HttpVersion.of(version);

        // then
        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(httpVersion.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void Http의_version이_존재하지_않으면_예외를_발생시킨다() {
        // given
        String version = "NOT_EXIST";

        // when & then
        assertThatThrownBy(() -> HttpVersion.of(version))
                .isInstanceOf(InvalidHttpVersionException.class);
    }
}
