package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("HttpRequestLine 테스트")
class HttpRequestLineTest {

    @DisplayName("유효한 값을 입력하면 HttpRequestLine 인스턴스를 생성해 반환한다.")
    @Test
    void createHttpRequestLineInstance() {
        // Given
        final String input = "GET /index.html HTTP/1.1";

        // When
        final HttpRequestLine httpRequestLine = new HttpRequestLine(input);

        // Then
        assertThat(httpRequestLine).isNotNull();
    }

    @DisplayName("생성자에 null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpRequestLineIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Http Request Line은 null 혹은 빈 값이 입력될 수 없습니다. - " + input);
    }

    @DisplayName("입력된 Request Line이 공백을 구분으로 총 3개의 필드로 이루어져 있지 않다면 예외를 발생시킨다.")
    @ValueSource(strings = {"GET", "GET /index.html", "GET,/index.html,HTTP/1.1"})
    @ParameterizedTest
    void validateHttpRequestLineFieldsCount(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestLine(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP Request Line 필드 개수입니다. - " + input);
    }
}
