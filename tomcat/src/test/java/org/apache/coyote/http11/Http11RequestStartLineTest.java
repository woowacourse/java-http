package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestStartLineTest {

    @DisplayName("HTTP 버전이 1.1이 아니면 생성할 수 없다.")
    @Test
    void validateVersion() {
        assertThatThrownBy(() -> Http11StartLine.from("GET /index.html HTTP/1.0"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("StartLine에서 Http Method와 Request Target을 추출한다.")
    @Test
    void from() {
        Http11StartLine http11StartLine = Http11StartLine.from("GET /index.html HTTP/1.1");

        assertAll(
                () -> Assertions.assertThat(http11StartLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> Assertions.assertThat(http11StartLine.getRequestTarget().getEndPoint()).isEqualTo("/index.html")
        );
    }
}
