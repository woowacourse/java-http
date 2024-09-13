package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("헤더가 없을 경우(빈 리스트) 헤더 값에는 아무것도 저장되지 않는다.")
    @Test
    void emptyHeaders() {
        // given
        List<String> headerLines = List.of();

        // when
        HttpHeaders headers = HttpHeaders.from(headerLines);

        // then
        assertThat(headers.getHeaders()).isEmpty();
    }

    @DisplayName("헤더가 없을 경우(빈 리스트) 헤더 값에는 아무것도 저장되지 않는다.")
    @ParameterizedTest
    @ValueSource(strings = {"header:", "header:   ", ": header", " : header", "header; header"})
    void wrongHeader(String header) {
        // given
        List<String> headerLines = List.of(header);

        // when
        HttpHeaders headers = HttpHeaders.from(headerLines);

        // then
        assertThat(headers.getHeaders()).isEmpty();
    }
}
