package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class RequestLineTest {

    static Stream<Arguments> testDataForRequestLine() {
        return Stream.of(
                Arguments.of("GET /index.html HTTP/1.1", "GET", "/index.html", "HTTP/1.1"),
                Arguments.of("POST /register HTTP/1.1", "POST", "/register.html", "HTTP/1.1")
        );
    }

    @DisplayName("RequestLine을 구성할 수 있다.")
    @MethodSource("testDataForRequestLine")
    @ParameterizedTest
    void testRequestLine(final String rawRequestLine, final String method, final String requestUri, final String version) {
        // given & when
        final RequestLine requestLine = new RequestLine(rawRequestLine);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(method),
                () -> assertThat(requestLine.getRequestUri()).isEqualTo(requestUri),
                () -> assertThat(requestLine.getVersion()).isEqualTo(version)
        );
    }

    @DisplayName("RequestLine은 null일 수 없다.")
    @Test
    void testThrowErrorIfRequestLineNull() {
        // given & when & then
        assertThatThrownBy(() -> new RequestLine(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request Line은 비어있을 수 없습니다.");
    }

    @DisplayName("RequestLine은 비어있을 수 없다.")
    @Test
    void testThrowErrorIfRequestLineBlank() {
        // given & when & then
        assertThatThrownBy(() -> new RequestLine(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request Line은 비어있을 수 없습니다.");
    }

    @DisplayName("RequestLine의 길이는 반드시 3이어야 한다.")
    @ValueSource(strings = {"GET /index.html", "POST /index.html HTTP/1.1 extra"})
    @ParameterizedTest
    void testThrowErrorIfRequestLineElementCountNotThree(final String requestLine) {
        // given & when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request Line 형식이 올바르지 않습니다. 형식은 'METHOD PATH PROTOCOL_VERSION'이어야 합니다.");
    }
}
