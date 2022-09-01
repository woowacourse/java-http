package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(value = {"css,CSS", "js,JS", "html,HTML", "ico,ICO", "etc,HTML"})
    void of(String fileFormat, ContentType expected) {
        // given & when & then
        assertThat(ContentType.of(fileFormat)).isEqualTo(expected);
    }
}
