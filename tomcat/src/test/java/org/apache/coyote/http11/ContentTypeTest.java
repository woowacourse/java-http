package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    void testToString() {
        //given
        final var contentType = new ContentType("text/html", "utf-8");

        //when
        final var actual = contentType.toString();

        //then
        assertThat(actual).isEqualTo("Content-Type: text/html;charset=utf-8 ");
    }

}
