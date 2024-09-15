package org.apache.catalina.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineTest {

    private final StatusLine statusLine = new StatusLine(Status.OK);

    @DisplayName("프로토콜 버전을 반환한다.")
    @Test
    void getProtocolVersion() {
        assertThat(statusLine.getProtocolVersion()).isEqualTo("HTTP/1.1");
    }

    @DisplayName("상태코드를 반환한다.")
    @Test
    void getStatusCode() {
        assertThat(statusLine.getStatusCode()).isEqualTo(200);
    }

    @DisplayName("상태 메시지를 반환한다.")
    @Test
    void getStatusMessage() {
        assertThat(statusLine.getStatusMessage()).isEqualTo("OK");
    }
}
