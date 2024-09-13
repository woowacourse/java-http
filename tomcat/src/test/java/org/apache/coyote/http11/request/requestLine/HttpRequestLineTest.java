package org.apache.coyote.http11.request.requestLine;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestLineTest {

    @DisplayName("RequestLine이 3개의 파라미터가 없으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET / ", "GET / ", " ", "GET "})
    void throw_exception_When_no_three_params(String requestLine) {
        assertThatThrownBy(() -> HttpRequestLine.toHttpRequestLine(requestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 RequestLine 형식입니다.");
    }
}
