package org.apache.coyote.http11.response;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    void findBy() throws NotFoundException {
        // given
        final String content = "index.html";

        // when
        final ContentType actual = ContentType.findBy(content);

        // then
        assertThat(actual).isEqualTo(ContentType.HTML);
    }

    @Test
    void findByElseContent() throws NotFoundException {
        // given
        final String elseContent = "index.else";

        // when
        final ContentType actual = ContentType.findBy(elseContent);

        // then
        assertThat(actual).isEqualTo(ContentType.HTML);
    }
}
