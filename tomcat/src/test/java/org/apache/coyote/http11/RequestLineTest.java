package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestLine 테스트")
class RequestLineTest {

    @DisplayName("Request Line 파싱에 성공한다.")
    @Test
    void parseRequestLine() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        assertAll(
                () -> assertThat(line.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(line.getUrl()).isEqualTo("/index.html"),
                () -> assertThat(line.getVersion()).isEqualTo("HTTP/1.1")
        );
    }
}
