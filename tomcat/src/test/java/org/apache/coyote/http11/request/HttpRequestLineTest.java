package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력을 HttpMethod, Uri, QueryString으로 파싱하여 저장한다.")
    void from() {
        // given
        final String rawStartLine = "GET /path?name=eve HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawStartLine);

        // then
        assertAll(() -> {
            assertThat(requestLine.isGet()).isTrue();
            assertThat(requestLine.getPath()).isEqualTo("/path");
            assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
            assertThat(requestLine.getQueryString()).isEqualTo("name=eve");
        });
    }
}