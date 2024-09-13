package org.apache.coyote.http11.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestLineTest {
    @DisplayName("주어진 request line string을 파싱한다.")
    @Test
    void from() {
        // given
        String requestLine = "POST /register HTTP/1.1 ";

        // when
        RequestLine result = RequestLine.from(requestLine);

        // then
        assertAll(
                () -> assertThat(result.getUri()).isEqualTo("/register"),
                () -> assertThat(result.getMethod()).isEqualTo(HttpMethod.POST)
        );
    }

    @DisplayName("주어진 request line string을 파싱한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET HTTP/1.1", "WRONG / HTTP/1.1", "GET / HTTP/0.9"})
    void wrongRequestLine() {
        // given
        String requestLine = "POST /register HTTP/1.1 ";

        // when
        RequestLine result = RequestLine.from(requestLine);

        // then
        assertAll(
                () -> assertThat(result.getUri()).isEqualTo("/register"),
                () -> assertThat(result.getMethod()).isEqualTo(HttpMethod.POST)
        );
    }
}
