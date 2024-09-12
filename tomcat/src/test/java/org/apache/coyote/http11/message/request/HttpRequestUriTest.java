package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("HttpRequestUri 테스트")
class HttpRequestUriTest {

    @DisplayName("유효한 값이 생성자에 입력되면 인스턴스를 생성한다.")
    @Test
    void createHttpRequestUriInstance() {
        // Given
        final String input = "/web/index.html";

        // When
        final HttpRequestUri httpRequestUri = new HttpRequestUri(input);

        // Then
        assertThat(httpRequestUri).isNotNull();
    }

    @DisplayName("Null 혹은 빈 값이 생성자에 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateRequestUriValueIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequestUri(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request URI 값으로 null 혹은 빈 값이 입력될 수 없습니다. - " + input);
    }

    @DisplayName("/로 시작하지 않는 경로 값이 생성자에 입력되면 예외를 발생시킨다.")
    @Test
    void validateRequestUriStartWithSlash() {
        // Given
        final String input = "index.html";

        // When & Then
        assertThatThrownBy(() -> new HttpRequestUri(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request URI 값은 /로 시작해야합니다. - " + input);
    }
}
