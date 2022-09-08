package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {

    @DisplayName("HttpMethod 객체를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void from(final String value) {
        // given, when, then
        assertThatCode(() -> HttpMethod.from(value))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 HttpMethod로 생성할 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"SOME", "GIVE", "SEND"})
    void from_throwsException_ifInvalidHttpMethod(final String value) {
        // given, when, then
        assertThatCode(() -> HttpMethod.from(value))
                .isInstanceOf(InvalidHttpRequestException.class);
    }
}
