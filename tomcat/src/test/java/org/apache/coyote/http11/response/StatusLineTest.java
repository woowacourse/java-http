package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    @DisplayName("상태 라인을 생성할 수 있어야 한다")
    void buildStatusLineResponse() {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);

        String result = statusLine.buildStatusLineResponse();

        assertThat(result).isEqualTo("HTTP/1.1 200 OK ");
    }

    @Test
    @DisplayName("상태 코드를 변경할 수 있어야 한다")
    void setHttpStatus() {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);

        statusLine.setHttpStatus(HttpStatus.FOUND);

        String result = statusLine.buildStatusLineResponse();

        assertThat(result).isEqualTo("HTTP/1.1 302 Found ");
    }
}
