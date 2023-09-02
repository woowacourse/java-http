package org.apache.coyote.common;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpVersionTest {

    @Test
    void HTTP_버전을_입력된_문자열을_통해서_조회할_수_있다() {
        // given
        final String inputSource = "HTTP/1.1";

        // when
        final HttpVersion foundHttpVersion = HttpVersion.from(inputSource);

        // then
        assertThat(foundHttpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }
}
