package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ContentTypeTest {

    @Test
    void 파일_확장자로_content_type을_반환한다() {
        // given
        String fileExtension = "html";

        // when
        ContentType contentType = ContentType.parseContentType(fileExtension);

        // then
        assertThat(contentType).isEqualTo(ContentType.HTML);
    }
}
