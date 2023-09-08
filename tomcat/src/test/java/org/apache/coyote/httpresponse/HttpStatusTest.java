package org.apache.coyote.httpresponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class HttpStatusTest {

    @Test
    void 올바른_형식으로_http_status_를_응답한다() {
        // given
        final HttpStatus httpStatus = HttpStatus.CREATED;

        // when
        final String actual = httpStatus.getHttpStatus();
        final String expected = "201 Created";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
