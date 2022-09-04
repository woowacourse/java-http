package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    void getStatusLine() {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);

        assertThat(statusLine.getStatusLine()).isEqualTo("HTTP/1.1 200 OK ");
    }
}
