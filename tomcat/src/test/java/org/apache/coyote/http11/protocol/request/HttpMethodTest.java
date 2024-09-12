package org.apache.coyote.http11.protocol.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("Method 이름으로 HttpMethod 을 찾는다.")
    void findHttpMethodByName() {
        String methodName = "GET";

        HttpMethod method = HttpMethod.ofName(methodName);

        assertThat(method).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("Method 이름이 잘못된 경우 예외를 던진다.")
    void throwExceptionWhenInvalidMethodName() {
        String invalidMethodName = "INVALID";

        assertThatThrownBy(() -> HttpMethod.ofName(invalidMethodName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HTTP method: INVALID");
    }
}
