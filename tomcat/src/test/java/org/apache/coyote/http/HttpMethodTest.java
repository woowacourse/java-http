package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class HttpMethodTest {

    @Test
    void 이름으로_GET_메서드를_찾을_수_있다() {
        // given
        String methodName = "GET";

        // when
        HttpMethod method = HttpMethod.findByName(methodName);

        // then
        assertThat(method).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 이름으로_POST_메서드를_찾을_수_있다() {
        // given
        String methodName = "POST";

        // when
        HttpMethod method = HttpMethod.findByName(methodName);

        // then
        assertThat(method).isEqualTo(HttpMethod.POST);
    }

    @Test
    void 지원하지_않는_메서드를_찾으려_하면_예외가_발생한다() {
        // given
        String unsupportedMethodName = "DELETE";

        // when & then
        assertThatThrownBy(() -> HttpMethod.findByName(unsupportedMethodName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 HTTP 메소드입니다.");
    }

    @Test
    void 메서드_이름이_대소문자를_구분한다() {
        // given
        String lowerCaseMethod = "get";

        // when & then
        assertThatThrownBy(() -> HttpMethod.findByName(lowerCaseMethod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 HTTP 메소드입니다.");
    }
}
