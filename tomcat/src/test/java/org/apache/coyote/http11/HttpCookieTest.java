package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.apache.coyote.http11.header.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 쿠키")
class HttpCookieTest {

    @DisplayName("HTTP 쿠키는 형식이 잘못되면 예외가 발생한다.")
    @Test
    void validateCookieFormat() {
        // given
        List<String> headerRequest = List.of(
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: key:value; "
        );
        HttpHeader header = HttpHeader.from(headerRequest);

        // when & then
        assertThatThrownBy(() -> HttpCookie.from(header))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 쿠키는 key=value 형식이어야 합니다.");
    }
}
