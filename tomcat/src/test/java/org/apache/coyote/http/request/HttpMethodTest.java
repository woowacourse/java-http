package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @Test
    @DisplayName("올바르지 않은 HTTP METHOD 인 경우 False 를 반환한다.")
    void validateMethod1() {
        String test = "GETT";

        assertThat(HttpMethod.isHttpMethod(test)).isFalse();
    }

    @Test
    @DisplayName("올바르지 않은 HTTP METHOD 로 찾는 경우 예외를 발생한다.")
    void validateMethod2() {
        String test = "GETT";

        assertThatThrownBy(() -> HttpMethod.findMethodByMethodName(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown http method: GETT");
    }
}
