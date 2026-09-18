package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpHeadersTest {
    @Test
    void parseHeaders() {
        // given
        final List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive ");

        // when
        final HttpHeaders headers = HttpHeaders.from(headerLines);

        // then
        assertThat(headers.get("Host")).isEqualTo("localhost:8080");
        assertThat(headers.get("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void headerNameIsCaseInsensitive() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("content-length: 10"));

        // when & then
        assertThat(headers.get("Content-Length")).isEqualTo("10");
        assertThat(headers.getContentLength()).isEqualTo(10);
    }

    @Test
    void contentLengthDefaultsToZero() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Host: localhost:8080"));

        // when & then
        assertThat(headers.getContentLength()).isZero();
    }

    @Test
    void invalidContentLength() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Length: abc"));

        // when & then
        assertThatThrownBy(headers::getContentLength)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 Content-Length 값입니다: abc");
    }

    @Test
    void negativeContentLength() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Length: -1"));

        // when & then
        assertThatThrownBy(headers::getContentLength)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 Content-Length 값입니다: -1");
    }

    @Test
    void headerWithoutDelimiter() {
        // given
        final List<String> headerLines = List.of("Host localhost");

        // when & then
        assertThatThrownBy(() -> HttpHeaders.from(headerLines))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 http 헤더 형태입니다: Host localhost");
    }
}
