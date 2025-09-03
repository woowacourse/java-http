package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.UnsupportedHttpMethodException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void 이름에_맞는_HttpMethod를_반환한다() {
        // given
        String name = "GET";

        // when
        HttpMethod actual = HttpMethod.from(name);

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 이름의_대소문자_구분없이_HttpMethod를_반환한다() {
        // given
        String name = "Get";

        // when
        HttpMethod actual = HttpMethod.from(name);

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 이름에_맞는_HttpMethod가_없다면_예외() {
        // given
        String name = "None";

        // when & then
        assertThatThrownBy(() -> HttpMethod.from(name))
                .isInstanceOf(UnsupportedHttpMethodException.class);
    }
}
