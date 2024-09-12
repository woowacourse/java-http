package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTypeTest {

    @DisplayName("유효한 Http Method 값이 입력되면 해당하는 Enum 객체를 반환한다.")
    @MethodSource("inputAndExceptForGetByValue")
    @ParameterizedTest
    void getByValue(final String input, final HttpMethodType expect) {
        // When
        final HttpMethodType httpMethodType = HttpMethodType.getByValue(input);

        // Then
        assertThat(httpMethodType).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndExceptForGetByValue() {
        return Stream.of(
                Arguments.of("GET", HttpMethodType.GET),
                Arguments.of("POST", HttpMethodType.POST),
                Arguments.of("PUT", HttpMethodType.PUT),
                Arguments.of("PATCH", HttpMethodType.PATCH),
                Arguments.of("DELETE", HttpMethodType.DELETE),
                Arguments.of("HEAD", HttpMethodType.HEAD),
                Arguments.of("OPTIONS", HttpMethodType.OPTIONS),
                Arguments.of("TRACE", HttpMethodType.TRACE),
                Arguments.of("CONNECT", HttpMethodType.CONNECT)
        );
    }

    @DisplayName("유효하지 않은 Http Method 값이 입력되면 예외를 발생시킨다.")
    @Test
    void getByValueWithInvalidInput() {
        // Given
        final String input = "KELLY";

        // When & Then
        assertThatThrownBy(() -> HttpMethodType.getByValue(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 HTTP Method 값 입니다. - " + input);
    }
}
