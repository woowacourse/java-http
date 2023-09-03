package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpMethod 테스트")
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
}
