package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    void renderStatusLine_상태줄을_만든다() {
        String expected = new String("HTTP/1.1 200 OK \r\n");

        String actual = StatusCode.OK.renderStatusLine();

        assertThat(actual).isEqualTo(expected);
    }
}