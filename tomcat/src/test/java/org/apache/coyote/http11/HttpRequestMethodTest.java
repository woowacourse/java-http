package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpRequestMethod 테스트")
class HttpRequestMethodTest {

    @Test
    void 문자에_따라_HttpRequestMethod를_생성한다() {
        // given
        final String value = "GET";

        // when
        final HttpRequestMethod httpRequestMethod = HttpRequestMethod.from(value);

        // then
        assertThat(httpRequestMethod).isEqualTo(HttpRequestMethod.GET);
    }
}
