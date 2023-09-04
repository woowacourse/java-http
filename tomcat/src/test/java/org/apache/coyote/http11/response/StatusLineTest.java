package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StatusLineTest {

    @Test
    void 상태라인을_생성한다() {
        // given
        StatusLine statusLine = new StatusLine(HttpStatusCode.OK);

        // when
        String response = statusLine.toString();

        // then
        assertThat(response).isEqualTo("HTTP/1.1 200 OK ");
    }
}
