package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotAllowedMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("request에 해당하는 MethodType을 올바르게 가져올 수 있다.")
    @Test
    void from() {
        // given
        final String request = "GET";
        final HttpMethod expected = HttpMethod.GET;

        // when
        final HttpMethod actual = HttpMethod.from(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("올바른 MethodType이 아니면 예외 처리한다.")
    @Test
    void from_notMethodType() {
        // given
        final String request = "NOT_METHOD_TYPE";

        // when & then
        assertThatThrownBy(() -> HttpMethod.from(request))
                .isInstanceOf(NotAllowedMethodException.class);
    }
}
