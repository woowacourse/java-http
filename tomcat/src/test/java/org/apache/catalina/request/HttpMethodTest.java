package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpMethodTest {
    @Nested
    @DisplayName("httpMethod enum 찾기")
    class of {
        @ParameterizedTest
        @EnumSource(value = HttpMethod.class)
        @DisplayName("성공 : enum에 존재하는 string 값일 경우 적절한 httpMethod 반환")
        void ofSuccess(HttpMethod expected) {
            HttpMethod actual = HttpMethod.of(expected.name());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 : 존재하지 않는 string 값인 경우 예외 발생")
        void ofFailByNotExistsValue() {
            String value = "string";
            assertThatCode(() -> HttpMethod.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(value + "는 존재하지 않는 HttpMethod 입니다.");
        }
    }
}
