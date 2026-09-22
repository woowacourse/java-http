package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 헤더")
class HttpHeadersTest {

    @Test
    @DisplayName("첫 번째 콜론을 기준으로 헤더 이름과 값을 분리한다")
    void parsesHeaderUsingFirstColon() {
        // given & when
        final HttpHeaders headers = HttpHeaders.from(List.of("Host: localhost:8080"));

        // then
        assertThat(headers.getFirst("Host")).contains("localhost:8080");
    }

    @Test
    @DisplayName("헤더 이름은 대소문자를 구분하지 않고 조회한다")
    void getsHeaderIgnoringCase() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Length: 10"));

        // when & then
        assertThat(headers.getFirst("content-length")).contains("10");
        assertThat(headers.getFirst("CONTENT-LENGTH")).contains("10");
    }
}
