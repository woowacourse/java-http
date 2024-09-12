package org.apache.coyote.http11.message.header;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("HttpHeaderAcceptType 테스트")
class HttpHeaderAcceptTypeTest {

    @DisplayName("유효한 Http header accept 값이 입력되면 해당하는 Enum 객체를 반환한다.")
    @MethodSource("inputAndExceptForGetByValue")
    @ParameterizedTest
    void getByValue(final String input, final HttpHeaderAcceptType expect) {
        // When
        final HttpHeaderAcceptType httpHeaderAcceptType = HttpHeaderAcceptType.getByValue(input);

        // Then
        assertThat(httpHeaderAcceptType).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndExceptForGetByValue() {
        return Stream.of(
                Arguments.of("text/plain", HttpHeaderAcceptType.PLAIN),
                Arguments.of("text/html", HttpHeaderAcceptType.HTML),
                Arguments.of("text/css", HttpHeaderAcceptType.CSS),
                Arguments.of("application/javascript", HttpHeaderAcceptType.JAVASCRIPT),
                Arguments.of("image/svg+xml", HttpHeaderAcceptType.SVG),
                Arguments.of("image/x-icon", HttpHeaderAcceptType.ICO),
                Arguments.of("*/*", HttpHeaderAcceptType.ALL_ACCEPT)
        );
    }

    @DisplayName("유효하지 않은 Http header accept 값이 입력되면 예외를 발생시킨다.")
    @Test
    void getByValueWithInvalidInput() {
        // Given
        final String input = "KELLY";

        // When & Then
        assertThatThrownBy(() -> HttpHeaderAcceptType.getByValue(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 HTTP Header Accept 값 입니다. - " + input);
    }
}
