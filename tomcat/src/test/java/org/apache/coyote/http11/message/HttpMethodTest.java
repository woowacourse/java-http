package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    @DisplayName("유효한 Http Method 값이 입력되면 해당하는 Enum 객체를 반환한다.")
    @MethodSource("inputAndExceptForGetByValue")
    @ParameterizedTest
    void getByValue(final String input, final HttpMethod expect) {
        // When
        final HttpMethod httpMethod = HttpMethod.getByValue(input);

        // Then
        assertThat(httpMethod).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndExceptForGetByValue() {
        return Stream.of(
                Arguments.of("GET", HttpMethod.GET),
                Arguments.of("POST", HttpMethod.POST),
                Arguments.of("PUT", HttpMethod.PUT),
                Arguments.of("PATCH", HttpMethod.PATCH),
                Arguments.of("DELETE", HttpMethod.DELETE),
                Arguments.of("HEAD", HttpMethod.HEAD),
                Arguments.of("OPTIONS", HttpMethod.OPTIONS),
                Arguments.of("TRACE", HttpMethod.TRACE),
                Arguments.of("CONNECT", HttpMethod.CONNECT)
        );
    }

    @DisplayName("유효하지 않은 Http Method 값이 입력되면 예외를 발생시킨다.")
    @Test
    void getByValueWithInvalidInput() {
        // Given
        final String input = "KELLY";

        // When & Then
        assertThatThrownBy(() -> HttpMethod.getByValue(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 HTTP Method 값 입니다. - " + input);
    }
}
