package org.apache.catalina.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StartLineTest {

    @DisplayName("요청의 시작 줄을 파싱한다.")
    @Test
    void parse() {
        StartLine startLine = StartLine.parse("POST /register HTTP/1.1");

        assertAll(
                () -> assertThat(startLine.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(startLine.getUri()).isEqualTo("/register")
        );
    }
}
