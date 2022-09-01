package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    void 알맞은_문자열_형식으로_출력되는지_확인한다() {
        // given
        final var statusLine = new StatusLine(HttpStatus.OK);

        // when
        String actual = statusLine.toString();

        // then
        final var expected = "HTTP/1.1 200 OK ";

        assertThat(actual).isEqualTo(expected);
    }
}
