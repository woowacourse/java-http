package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    void convertToString() {
        //given
        final var contentType = new ContentType("text/html", "utf-8");

        //when
        final var actual = contentType.convertToString();

        //then
        assertThat(actual).isEqualTo("text/html;charset=utf-8");
    }

}
