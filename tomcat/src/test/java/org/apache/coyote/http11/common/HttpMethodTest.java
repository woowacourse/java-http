package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.NotMatchedHttpMethodException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpMethodTest {

    @Test
    void Http의_method_이름으로_반환한다() {
        // given
        String method = "GET";

        // when
        HttpMethod httpMethod = HttpMethod.of(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void Http의_method가_소문자이더라도_맞는_값을_반환한다() {
        // given
        String method = "post";

        // when
        HttpMethod httpMethod = HttpMethod.of(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }

    @Test
    void Http의_method가_존재하지_않으면_예외를_발생시킨다() {
        // given
        String method = "NOT_EXIST";

        // when & then
        assertThatThrownBy(() -> HttpMethod.of(method))
                .isInstanceOf(NotMatchedHttpMethodException.class);
    }
}
