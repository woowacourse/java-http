package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("HttpRequestUri 테스트")
class HttpRequestUriTest {

    @DisplayName("유효한 값을 입력하면 HttpRequestUri 인스턴스를 생성해 반환한다.")
    @Test
    void createHttpRequestUriInstance() {
        // Given
        final String input = "/web/index.html";

        // When
        final HttpRequestUri httpRequestUri = new HttpRequestUri(input);

        // Then
        assertThat(httpRequestUri).isNotNull();
    }

    @DisplayName("생성자에 null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpRequestUriIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestUri(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("HTTP Request URI는 null 혹은 빈 값이 입력될 수 없습니다. - " + input);
    }

    @DisplayName("입력된 Request Uri가 /로 시작하지 않는다면 예외를 발생시킨다.")
    @Test
    void validateHttpRequestUriIsStartsWithSlash() {
        // Given
        final String input = "index.html";

        // When & Then
        assertThatThrownBy(() -> new HttpRequestUri(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("HTTP Request URI는 /로 시작해야합니다. - " + input);
    }
}
