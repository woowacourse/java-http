package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentLengthTest {

    @Test
    void testToString() {
        //given
        final var body = "Hello world!";

        //when
        final var contentLength = ContentLength.from(body);

        //then
        assertThat(contentLength).hasToString("Content-Length: 12");
    }

}
