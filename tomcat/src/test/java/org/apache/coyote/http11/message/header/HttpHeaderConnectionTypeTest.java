package org.apache.coyote.http11.message.header;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("HttpHeaderConnectionType 테스트")
class HttpHeaderConnectionTypeTest {

    @DisplayName("유효한 Http Header Connection 값이 입력되면 해당하는 Enum 객체를 반환한다.")
    @MethodSource("inputAndExceptForGetByValue")
    @ParameterizedTest
    void getByValue(final String input, final HttpHeaderConnectionType expect) {
        // When
        final HttpHeaderConnectionType httpHeaderConnectionType = HttpHeaderConnectionType.getByValue(input);

        // Then
        assertThat(httpHeaderConnectionType).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndExceptForGetByValue() {
        return Stream.of(
                Arguments.of("keep-alive", HttpHeaderConnectionType.KEEP_ALIVE),
                Arguments.of("close", HttpHeaderConnectionType.CLOSE),
                Arguments.of("Upgrade", HttpHeaderConnectionType.UPGRADE),
                Arguments.of("TE", HttpHeaderConnectionType.TE)
        );
    }

    @DisplayName("유효하지 않은 Http Header Connection 값이 입력되면 예외를 발생시킨다.")
    @Test
    void getByValueWithInvalidInput() {
        // Given
        final String input = "KELLY";

        // When & Then
        assertThatThrownBy(() -> HttpHeaderConnectionType.getByValue(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 HTTP Header Connection 값 입니다. - " + input);
    }
}
