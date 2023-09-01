package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class HttpStartLineTest {

    @Test
    void HTTP_요청_메세지_시작라인의_정보를_저장한다() {
        // given
        final String startLine = "GET /index.html HTTP/1.1";

        // when
        final HttpStartLine httpStartLine = HttpStartLine.from(startLine);

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpStartLine.getHttpRequestMethod()).isEqualTo(HttpRequestMethod.GET);
            assertThat(httpStartLine.getRequestURI()).isEqualTo("/index.html");
            assertThat(httpStartLine.getHttpVersion()).isEqualTo("HTTP/1.1");
        });
    }
}
