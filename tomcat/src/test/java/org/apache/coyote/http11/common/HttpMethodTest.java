package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpMethod 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void 문자에_따라_HttpRequestMethod를_생성한다() {
        // given
        final String value = "GET";

        // when
        final HttpMethod httpMethod = HttpMethod.from(value);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 지원하지_않는_HttpRequestMethod요청시_예외_발생() {
        // given
        final String value = "NOT_SUPPORTED";

        // when & then
        assertThatThrownBy(() -> HttpMethod.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다.");
    }
}
