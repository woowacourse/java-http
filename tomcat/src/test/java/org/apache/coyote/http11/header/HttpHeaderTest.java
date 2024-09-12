package org.apache.coyote.http11.header;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 헤더")
class HttpHeaderTest {

    @DisplayName("HTTP 헤더는 요청 형식이 잘못되면 예외가 발생한다.")
    @Test
    void validateHeaderFormat() {
        // given
        List<String> wrongRequest = List.of(
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: "
        );

        // when & then
        assertThatThrownBy(() -> HttpHeader.from(wrongRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 헤더는 key=value 형식이어야 합니다.");
    }
}
