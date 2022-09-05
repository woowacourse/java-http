package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.ContentType;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 일치하지_않는_확장자가_들어오면_TEXT_HTML을_반환한다() {
        // given
        String extension = "not_match";

        // when
        ContentType contentType = ContentType.fromExtension(extension);

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML);
    }
}
