package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    void toString_test() {
        // given
        StatusLine statusLine = StatusLine.http11(HttpStatus.OK);
        String expected = "HTTP/1.1 200 OK ";

        // when
        String actual = statusLine.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }

}