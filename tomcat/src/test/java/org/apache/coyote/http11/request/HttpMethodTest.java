package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidRequestMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("입력 문자열을 통해, Http Method를 생성할 수 있다.")
    void createHttpMethodFromInput() {
        String input = "GET";

        assertThat(HttpMethod.from(input)).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("잘못된 문자열을 입력하면, Http Method 생성 과정에서 예외가 발생한다.")
    void createHttpMethodFromInvalidInput() {
        String input = "INVALID";

        assertThatThrownBy(() -> HttpMethod.from(input))
                .isInstanceOf(InvalidRequestMethodException.class);
    }

}