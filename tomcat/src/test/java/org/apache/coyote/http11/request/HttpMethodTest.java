package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void Method_이름과_일치하면_Method를_반환한다() {
        // given
        String method = "GET";

        // when & then
        assertThat(HttpMethod.of(method)).isEqualTo(HttpMethod.GET);
    }

    @Test
    void Method_이름과_일치하지_않으면_Method를_반환한다() {
        // given
        String invalidMethod = "GeT";

        // when & then
        assertThatThrownBy(() -> HttpMethod.of(invalidMethod))
                .isInstanceOf(IllegalArgumentException.class);
    }
}