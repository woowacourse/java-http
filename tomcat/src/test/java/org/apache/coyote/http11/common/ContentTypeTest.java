package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ContentTypeTest {

    @Test
    void 확장자에_따라_해당하는_ContentType을_반환한다() {
        // given
        String extension = ".css";

        // when
        String actual = ContentType.getDetailfromExtension(extension);

        // then
        assertThat(actual).isEqualTo("text/css");
    }
}
