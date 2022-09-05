package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpVersionTest {

    @DisplayName("유효한 HttpVersion으로 객체를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"HTTP/0.9", "HTTP/1.0", "HTTP/1.1", "HTTP/2.0"})
    void from(final String version) {
        // given, when, then
        assertThatCode(() -> HttpVersion.from(version))
                .doesNotThrowAnyException();
    }

    @DisplayName("유효하지 않은 HttpVersion으로 객체를 생성할 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"TCP", "HTTP/1.5"})
    void from_throwsException_ifInvalidHttpVersion(final String invalidVersion) {
        // given, when, then
        assertThatCode(() -> HttpVersion.from(invalidVersion))
                .isInstanceOf(InvalidHttpRequestException.class);
    }
}
