package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @DisplayName("시작 라인을 메세지로 반환한다.")
    @Test
    void convertToMessage() {
        //given
        String expected = "HTTP/1.1 200 OK ";
        StatusLine statusLine = new StatusLine("HTTP/1.1");

        //when
        String result = statusLine.convertToMessage();

        //then
        assertThat(result).isEqualTo(expected);
    }
}
