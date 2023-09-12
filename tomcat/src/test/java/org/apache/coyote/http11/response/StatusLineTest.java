package org.apache.coyote.http11.response;

import org.apache.coyote.protocol.Protocol;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineTest {

    @Test
    void String으로_변환한다() {
        //given
        final StatusLine statusLine = new StatusLine(Protocol.HTTP11, StatusCode.OK);

        //when
        final String actual = statusLine.parseResponse();

        //then
        assertThat(actual).isEqualTo("HTTP/1.1 200 OK ");
    }
}
