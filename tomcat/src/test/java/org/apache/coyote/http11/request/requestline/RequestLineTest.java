package org.apache.coyote.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("request line 문자열을 parsing해 RequestLine 객체를 생성한다.")
    @Test
    void from() {
        final String rawRequestLine = "GET /login HTTP/1.1 ";
        final RequestLine requestLine = RequestLine.from(rawRequestLine);
        assertAll(
                () -> assertThat(requestLine.isPath("/login")).isTrue(),
                () -> assertThat(requestLine.isMethod(Method.GET)).isTrue()
        );
    }
}
