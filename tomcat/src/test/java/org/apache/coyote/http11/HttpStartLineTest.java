package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpStartLine 테스트")
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

    @Test
    void 잘못된_HTTP_요청_메세지_시작라인인_경우_예외_발생() {
        // given
        final String startLine = "GETHTTP/1.1";

        // when & then
        assertThatThrownBy(() -> HttpStartLine.from(startLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 라인의 토큰은 3개여야 합니다.");
    }
}
