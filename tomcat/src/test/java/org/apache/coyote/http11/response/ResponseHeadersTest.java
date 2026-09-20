package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.headers.ResponseHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseHeadersTest {

    @Test
    @DisplayName("추가한 헤더를 이름: 값 형태의 라인으로 반환한다.")
    void toLines() {
        // given
        ResponseHeaders headers = new ResponseHeaders();
        headers.add("Content-Type", "text/html;charset=utf-8");
        headers.add("Content-Length", "12");

        // when & then
        assertThat(headers.toLines()).containsExactly(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12"
        );
    }

    @Test
    @DisplayName("헤더는 추가한 순서를 유지한다.")
    void keepInsertionOrder() {
        // given
        ResponseHeaders headers = new ResponseHeaders();
        headers.add("Location", "/index.html");
        headers.add("Set-Cookie", "JSESSIONID=abc123");
        headers.add("Content-Length", "0");

        // when & then
        assertThat(headers.toLines()).containsExactly(
                "Location: /index.html",
                "Set-Cookie: JSESSIONID=abc123",
                "Content-Length: 0"
        );
    }

    @Test
    @DisplayName("헤더가 없으면 빈 라인 목록을 반환한다.")
    void emptyHeaders() {
        // given
        ResponseHeaders headers = new ResponseHeaders();

        // when & then
        assertThat(headers.toLines()).isEmpty();
    }

}
