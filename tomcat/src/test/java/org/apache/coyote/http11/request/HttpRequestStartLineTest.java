package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpRequestStartLine 테스트")
class HttpRequestStartLineTest {

    @Test
    void HTTP_요청_메세지_시작라인의_정보를_저장한다() {
        // given
        final String startLine = "GET /index.html HTTP/1.1";

        // when
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpRequestStartLine.getHttpRequestMethod()).isEqualTo(HttpRequestMethod.GET);
            assertThat(httpRequestStartLine.getRequestURI()).isEqualTo("/index.html");
            assertThat(httpRequestStartLine.getHttpVersion()).isEqualTo("HTTP/1.1");
        });
    }

    @Test
    void 잘못된_HTTP_요청_메세지_시작라인인_경우_예외_발생() {
        // given
        final String startLine = "GETHTTP/1.1";

        // when & then
        assertThatThrownBy(() -> HttpRequestStartLine.from(startLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 라인의 토큰은 3개여야 합니다.");
    }

    @Test
    void 쿼리파라미터와_URI를_분리하여_저장한다() {
        // given
        final String startLine = "GET /index.html?name=royce&password=p1234 HTTP/1.1";

        // when
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpRequestStartLine.getParam("name")).isEqualTo("royce");
            assertThat(httpRequestStartLine.getParam("password")).isEqualTo("p1234");
        });
    }
}
