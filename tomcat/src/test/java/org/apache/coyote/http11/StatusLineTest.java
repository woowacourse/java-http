package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    void HTTP_버전과_상태로_Status_Line을_만든다() {
        StatusLine statusLine = new StatusLine(
                HttpVersion.HTTP_1_1,
                HttpStatus.OK
        );

        assertThat(statusLine.serialize())
                .isEqualTo("HTTP/1.1 200 OK");
    }
}
