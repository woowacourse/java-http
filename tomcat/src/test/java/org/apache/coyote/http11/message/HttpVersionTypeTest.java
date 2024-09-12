package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("HttpVersion 테스트")
class HttpVersionTypeTest {

    @DisplayName("유효한 Http Version 값이 입력되면 해당하는 Enum 객체를 반환한다.")
    @MethodSource("inputAndExceptForGetByValue")
    @ParameterizedTest
    void getByValue(final String input, final HttpVersionType expect) {
        // When
        final HttpVersionType httpVersionType = HttpVersionType.getByValue(input);

        // Then
        assertThat(httpVersionType).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndExceptForGetByValue() {
        return Stream.of(
                Arguments.of("HTTP/0.9", HttpVersionType.HTTP_0_9),
                Arguments.of("HTTP/1.0", HttpVersionType.HTTP_1_0),
                Arguments.of("HTTP/1.1", HttpVersionType.HTTP_1_1),
                Arguments.of("HTTP/2", HttpVersionType.HTTP_2),
                Arguments.of("HTTP/3", HttpVersionType.HTTP_3)
        );
    }

    @DisplayName("유효하지 않은 Http Version 값이 입력되면 예외를 발생시킨다.")
    @Test
    void getByValueWithInvalidInput() {
        // Given
        final String input = "HTTP/1000000.3";

        // When & Then
        assertThatThrownBy(() -> HttpVersionType.getByValue(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 HTTP Version 값 입니다. - " + input);
    }
}
