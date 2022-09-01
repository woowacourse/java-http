package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.apache.coyote.http11.HttpMethod;
import org.junit.jupiter.api.Assertions;
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
                () -> assertThat(startLine.getUrl().getValue()).isEqualTo("/index.html"),
                () -> assertThat(startLine.getProtocol().value()).isEqualTo("HTTP/1.1")
        );
    }
}
