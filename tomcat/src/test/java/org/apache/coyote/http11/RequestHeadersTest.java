package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHeadersTest {

    @Test
    void 헤더를_파싱한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of(
                "Host: localhost:8080",
                "Accept: text/html"));

        assertThat(headers.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(headers.getHeader("Accept")).isEqualTo("text/html");
    }

    @Test
    void 헤더_이름은_대소문자를_구분하지_않고_조회한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Type: text/html"));

        assertThat(headers.getHeader("content-type")).isEqualTo("text/html");
        assertThat(headers.getHeader("CONTENT-TYPE")).isEqualTo("text/html");
    }

    @Test
    void 헤더_형식이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("InvalidHeader")))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void Content_Length를_숫자로_제공한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Length: 30"));

        assertThat(headers.getContentLength()).isEqualTo(30);
    }

    @Test
    void Content_Length가_없으면_0이다() {
        final RequestHeaders headers = RequestHeaders.from(List.of());

        assertThat(headers.getContentLength()).isZero();
    }

    @Test
    void Content_Length가_숫자가_아니면_예외가_발생한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Length: abc"));

        assertThatThrownBy(headers::getContentLength)
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void Content_Length가_음수면_예외가_발생한다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("Content-Length: -1"));

        assertThatThrownBy(headers::getContentLength)
                .isInstanceOf(HttpRequestParseException.class);
    }
}
