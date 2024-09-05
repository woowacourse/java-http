package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @DisplayName("시작 라인을 메세지로 반환한다.")
    @Test
    void getStatusLineMessage() {
        //given
        String expected = "HTTP/1.1 200 OK ";
        StatusLine statusLine = new StatusLine();

        //when
        String result = statusLine.getStatusLineMessage();

        //then
        assertThat(result).isEqualTo(expected);
    }
}
