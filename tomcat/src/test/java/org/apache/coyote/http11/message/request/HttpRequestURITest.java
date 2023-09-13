package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestURITest {

    @Test
    void 절대경로를_반환할_수_있다() {
        // given
        RequestURI requestURI = RequestURI.from("teo?query=123");

        // when
        String absolutePath = requestURI.absolutePath();

        // then
        assertThat(absolutePath).isEqualTo("teo");
    }

    @Test
    void 쿼리_파라미터가_존재하는지_체크할_수_있다() {
        // given
        RequestURI requestURI = RequestURI.from("teo?query=123");

        // when
        boolean hasQueryParameters = requestURI.hasQueryParameters();

        // then
        assertThat(hasQueryParameters).isTrue();
    }
}
