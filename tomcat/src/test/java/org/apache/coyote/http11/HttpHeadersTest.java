package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더를 올바르게 파싱해 변환한다.")
    void convertHeader() {
        List<String> headerLines = List.of(
                "Content-Type: text/html",
                "Authorization: Bearer 12345",
                "Many-Colons: a:b:c:d"
        );
        HttpHeaders httpHeaders = new HttpHeaders(headerLines);
        assertThat(httpHeaders.getFields()).hasSize(3);
    }

    @Test
    @DisplayName("올바르지 않은 헤더 줄이 존재하는 경우, 해당 줄은 무시된다.")
    void ignoreInvalidHeaderLines() {
        List<String> headerLines = List.of(
                "Content-Type: text/html",
                "No colon headerline"
        );
        HttpHeaders httpHeaders = new HttpHeaders(headerLines);
        assertThat(httpHeaders.getFields()).hasSize(1);
    }

    @Test
    @DisplayName("Content-Length가 존재하지 않는 경우, 기본값인 0을 반환한다.")
    void defaultContentLength() {
        HttpHeaders httpHeaders = new HttpHeaders();
        assertThat(httpHeaders.getContentLength()).isZero();
    }
}
