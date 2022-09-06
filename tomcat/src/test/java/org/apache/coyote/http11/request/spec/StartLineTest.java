package org.apache.coyote.http11.request.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    @DisplayName("HTTP 요청 첫 라인을 받아 메서드, URL, 프로토콜을 파싱해 객체를 생성한다.")
    void createStartLine() {
        final String line = "GET /index.html HTTP/1.1\r\n";

        StartLine startLine = StartLine.from(line);

        assertAll(
                () -> assertThat(startLine).isNotNull(),
                () -> assertThat(startLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(startLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(startLine.getProtocol().value()).isEqualTo("HTTP/1.1")
        );
    }
}
