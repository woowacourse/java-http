package org.apache.coyote.http11.request.requestLine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpMethodTest {

    @Test
    @DisplayName("문자열로 주어진 Http 메서드를 HttpMethod 타입으로 변환한다")
    void stringToHttpMethod() {
        // given
        final String httpMethod = "get";

        // when
        final HttpMethod actual = HttpMethod.from(httpMethod);

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }
}
