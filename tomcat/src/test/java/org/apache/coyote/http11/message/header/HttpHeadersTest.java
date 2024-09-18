package org.apache.coyote.http11.message.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeadersTest {

    @DisplayName("유효한 값을 입력하면 HttpHeaders 인스턴스를 생성해 반환한다.")
    @Test
    void createHttpHeadersInstance() {
        // Given
        final String input = """
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46""";

        // When
        final HttpHeaders httpHeaders = new HttpHeaders(input);

        // Then
        assertThat(httpHeaders).isNotNull();
    }

    @DisplayName("생성자에 null 혹은 빈 값이 입력되면 예외를 발생시킨다.")
    @NullAndEmptySource
    @ParameterizedTest
    void validateHttpHeadersIsNullOrBlank(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpHeaders(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("HTTP Headers는 null 혹은 빈 값이 입력될 수 없습니다. - " + input);
    }

    @DisplayName("{key: value} 형식이 아닌 헤더 값이 입력되면 예외를 발생시킨다")
    @ValueSource(strings = {"host, localhost", "host", "host-localhost"})
    @ParameterizedTest
    void validateHeaderFieldKeyAndValueFormat(final String input) {
        // When & Then
        assertThatThrownBy(() -> new HttpHeaders(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP Header Field입니다. - " + input);
    }
}
