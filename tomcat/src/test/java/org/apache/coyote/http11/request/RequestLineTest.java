package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.common.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestLineTest {

    @DisplayName("HTTP Request Line을 파싱하여 객체를 올바르게 생성한다.")
    @Test
    void parseAndInstantiate() {
        // given
        String rawLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine requestLine = RequestLine.from(rawLine);

        // then
        assertAll(
                () -> assertThat(requestLine.method()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.path()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.version()).isEqualTo("HTTP/1.1")
        );
    }

    @DisplayName("잘못된 HTTP Request Line을 파싱하면 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "GET /index.html", "&Y&*#RI"})
    void throwsExceptionWhenInvalidRequestLine(String invalidRequestLine) {
        assertThatThrownBy(() -> RequestLine.from(invalidRequestLine))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
