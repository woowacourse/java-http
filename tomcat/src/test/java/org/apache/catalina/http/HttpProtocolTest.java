package org.apache.catalina.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.catalina.http.HttpProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpProtocolTest {
    @Nested
    @DisplayName("HttpProtocol enum 찾기")
    class of {
        @ParameterizedTest
        @EnumSource(value = HttpProtocol.class)
        @DisplayName("성공 : enum에 존재하는 string 값일 경우 적절한 HttpProtocol 반환")
        void ofSuccess(HttpProtocol expected) {
            HttpProtocol actual = HttpProtocol.of(expected.name());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 : 존재하지 않는 string 값인 경우 예외 발생")
        void ofFailByNotExistsValue() {
            String value = "httpss";
            assertThatCode(() -> HttpProtocol.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(value + "는 존재하지 않는 HttpProtocol 입니다.");
        }
    }
}
