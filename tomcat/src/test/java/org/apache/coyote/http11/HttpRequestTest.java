package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 요청")
class HttpRequestTest {

    @DisplayName("HTTP 요청의 본문 형식이 잘못되면 예외가 발생한다.")
    @Test
    void validateBodyFormat() {
        // given
        String wrongBody = "key=";
        List<String> headers = List.of(
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-type: application/x-www-form-urlencoded ",
                "Content-Length: " + wrongBody.getBytes().length + " "
        );

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(headers, wrongBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 본문은 key=value 형식이어야 합니다.");
    }
}
