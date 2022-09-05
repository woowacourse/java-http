package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.UnsupportedContentTypeException;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 확장자에_맞는_Content_Type을_반환한다() {
        // given
        final var extension = "js";

        // when
        final var actual = ContentType.extension(extension);

        // then
        final var expected = ContentType.APPLICATION_JS;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 지원하지_않는_확장자인_경우_예외를_던진다() {
        // given
        final var extension = "jss";

        // when & then
        assertThatThrownBy(() -> ContentType.extension(extension))
                .isInstanceOf(UnsupportedContentTypeException.class);
    }
}
