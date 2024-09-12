package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("HttpRequest 테스트")
class HttpRequestTest {

    @DisplayName("유효한 값을 입력하면 인스턴스를 반환한다.")
    @Test
    void createHttpRequestInstance() {
        // Given
        final String input = """
                POST /submit-form HTTP/1.1
                Host: www.example.com
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 34
                           
                name=John+Doe&email=john%40example.com""";

        // When
        final HttpRequest httpRequest = new HttpRequest(input);

        // Then
        assertThat(httpRequest).isNotNull();
    }

    @DisplayName("생성자에 Null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpRequestValueIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpRequest(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Http Request 값은 Null 혹은 빈 값이 될 수 없습니다. - " + input);
    }
}
