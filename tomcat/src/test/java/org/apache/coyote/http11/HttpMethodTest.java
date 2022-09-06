package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("HttpMethod Enum의")
class HttpMethodTest {

    @Nested
    @DisplayName("findByName 메서드는")
    @TestInstance(Lifecycle.PER_CLASS)
    class FindByName {

        @ParameterizedTest(name = "{0} 요청 시 {1} 타입을 반환한다.")
        @MethodSource("httpMethodArguments")
        @DisplayName("주어진 이름과 일치하는 HTTP 메서드 타입을 반환한다.")
        void success(final String methodName, final HttpMethod expected) {
            // given & when
            final HttpMethod actual = HttpMethod.findByName(methodName);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("주어진 이름과 일치하는 HTTP 메서드 타입이 존재하지 않는 경우 예외를 던진다.")
        void invalidHttpMethodName_ExceptionThrown() {
            // given
            final String name = "METHOD";

            // when & then
            assertThatThrownBy(() -> HttpMethod.findByName(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 HTTP 요청 메서드 타입이 아닙니다.");
        }

        private Stream<Arguments> httpMethodArguments() {
            return Stream.of(Arguments.of("GET", HttpMethod.GET),
                    Arguments.of("HEAD", HttpMethod.HEAD),
                    Arguments.of("post", HttpMethod.POST),
                    Arguments.of("PUT", HttpMethod.PUT),
                    Arguments.of("PATCH", HttpMethod.PATCH),
                    Arguments.of("DELETE", HttpMethod.DELETE));
        }
    }
}
