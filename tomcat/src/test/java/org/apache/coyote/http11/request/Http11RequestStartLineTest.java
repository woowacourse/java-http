package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class Http11RequestStartLineTest {

    @DisplayName("HTTP 버전이 1.1이 아니면 생성할 수 없다.")
    @Test
    void validateVersion() {
        assertThatThrownBy(() -> Http11RequestStartLine.from("GET /index.html HTTP/1.0"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("StartLine에서 Http Method와 Request Target을 추출한다.")
    @Test
    void from() {
        Http11RequestStartLine http11StartLine = Http11RequestStartLine.from("GET /index.html HTTP/1.1");

        assertAll(
                () -> Assertions.assertThat(http11StartLine.getMethod()).isEqualTo(Http11Method.GET),
                () -> Assertions.assertThat(http11StartLine.getRequestTarget().getEndPoint()).isEqualTo("/index.html")
        );
    }

    @DisplayName("문자가 널이거나 비어있을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {" ", "", "  "})
    @NullSource
    void validateBlank(String value) {
        assertThatThrownBy(() -> Http11RequestStartLine.from(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
