package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpVersionTest {

    @Test
    void HTTP_버전에_맞는_객체를_반환할_수_있다() {
        // given
        String rawHttpVersion = "HTTP/1.1";

        // when
        HttpVersion httpVersion = HttpVersion.from(rawHttpVersion);

        // then
        assertThat(httpVersion).isEqualTo(HttpVersion.V_1_1);
    }
}
