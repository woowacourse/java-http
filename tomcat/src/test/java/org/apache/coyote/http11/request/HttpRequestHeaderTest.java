package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestHeaderTest {

    @DisplayName("HttpRequestHeader를 생성할 수 있다.")
    @Test
    void toHttpRequestHeader() {
        // given
        List<String> headers = List.of("Host: localhost:8080/", "Set-Cookies: 123");

        assertDoesNotThrow(()->HttpRequestHeader.toHttpRequestHeader(headers));
    }

    @DisplayName("Header양식이 알맞지 않은경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"Host=a", "Host-a", "a","Host:localhost"})
    void throw_exception_When_form_incorrect(String incorrectHeaderFrom) {
        // given
        List<String> incorrectHeaderFroms = List.of(incorrectHeaderFrom);

        // when-then
        assertThatThrownBy(() -> HttpRequestHeader.toHttpRequestHeader(incorrectHeaderFroms))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 HTTP/1.1 헤더 양식입니다.");
    }

    @DisplayName("Header에 Host가 없을 경우 예외가 발생한다.")
    @Test
    void throw_exception_When_no_host() {
        // given
        List<String> incorrectHeaderFroms = List.of("Set-Cookies: noHostTest");

        // when-then
        assertThatThrownBy(() -> HttpRequestHeader.toHttpRequestHeader(incorrectHeaderFroms))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 HTTP/1.1 헤더 양식입니다.");
    }
}
