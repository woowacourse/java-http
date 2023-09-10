package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundHttpMethodException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MethodTest {

    @Test
    void findBy() {
        // given
        final String method = "GET";

        // when
        final Method actual = Method.findBy(method);

        // then
        assertThat(actual).isEqualTo(Method.GET);
    }

    @Test
    void findByInvalidName() {
        // given
        final String method = "INVALID";

        // when & then
        assertThatThrownBy(() -> Method.findBy(method)).isInstanceOf(NotFoundHttpMethodException.class)
                                                       .hasMessage("해당 요청에 대한 메서드를 찾지 못했습니다.");
    }
}
