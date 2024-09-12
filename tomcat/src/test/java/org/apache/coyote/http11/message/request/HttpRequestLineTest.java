package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("HttpRequestLine 테스트")
class HttpRequestLineTest {

    @DisplayName("유효한 값이 생성자에 입력되면 인스턴스를 생성한다.")
    @Test
    void createHttpRequestLineInstance() {
        // Given
        final String input = "GET /index.html HTTP/1.1";

        // When
        final HttpRequestLine httpRequestLine = new HttpRequestLine(input);

        // Then
        assertThat(httpRequestLine).isNotNull();
    }

    @DisplayName("Null 혹은 빈 값이 생성자에 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpRequestLineValue(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP Request Line 값 입니다. - " + input);
    }

    @DisplayName("Request Line 필드들을 분리했을 때 분리된 필드 개수가 3개가 아니라면 예외를 발생시킨다.")
    @MethodSource("invalidRequestLineValueAndParsedFieldsSize")
    @ParameterizedTest
    void validateParsedHttpRequestLineFieldsSize(final String input, final int parsedFieldsSize) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP Request Line 필드 개수입니다. - " + parsedFieldsSize);
    }

    private static Stream<Arguments> invalidRequestLineValueAndParsedFieldsSize() {
        return Stream.of(
                Arguments.of("GET", 1),
                Arguments.of("GET /index.html", 2),
                Arguments.of("GET,/index.html,HTTP/1.1", 1)
        );
    }
}
