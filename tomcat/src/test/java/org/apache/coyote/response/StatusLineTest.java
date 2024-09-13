package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusLineTest {
    @Test
    void statusLine의_응답을_생성한다() {
        // given
        StatusLine statusLine = new StatusLine();

        // when
        String response = statusLine.getResponse();

        // then
        assertThat(response).isEqualTo("HTTP/1.1 200 OK");
    }
}
