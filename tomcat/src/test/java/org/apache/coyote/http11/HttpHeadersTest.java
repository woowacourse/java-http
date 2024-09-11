package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {
    @DisplayName("주어진 헤더 string을 파싱한다.")
    @Test
    void from() {
        // given
        List<String> headerLines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        // when
        HttpHeaders headers = HttpHeaders.from(headerLines);

        // then
        assertAll(
                () -> assertThat(headers.getHeaders()).hasSize(2),
                () -> assertThat(headers.getHeaders()).contains(Map.entry("Host", "localhost:8080"), Map.entry("Connection", "keep-alive"))
        );
    }
}
