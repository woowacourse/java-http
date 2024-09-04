package org.apache.coyote.http11;

import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.method.NotFoundHttpMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {
    @Test
    @DisplayName("지원하지 않는 메소드는 예외를 발생한다.")
    void throw_exception_not_support_method() {
        final String method = "SOME";
        assertThatThrownBy(() -> HttpMethod.from(method))
                .isInstanceOf(NotFoundHttpMethodException.class);
    }

    @Test
    @DisplayName("메소드 명은 대소문자 상관이 없다.")
    void equal_when_upper_and_lower_case() {
        final String method1 = "get";
        final String method2 = "GET";

        assertThat(HttpMethod.from(method1)).isEqualTo(HttpMethod.from(method2));
    }
}
