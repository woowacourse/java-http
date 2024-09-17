package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Http 헤더 테스트")
class HttpHeadersTest {

    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
    }

    @Test
    @DisplayName("헤더를 추가하고 조회할 수 있다.")
    void putAndGetHeader() {
        // given
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, "text/html");

        // when & then
        assertThat(httpHeaders.getHeaderValue(HttpHeaderName.CONTENT_TYPE)).isEqualTo("text/html");
    }

    @Test
    @DisplayName("헤더 문자열을 받아 헤더를 추가할 수 있다.")
    void putHeaderFromString() {
        // given
        String headerLine = "Content-Length: 12345";

        // when
        httpHeaders.putHeader(headerLine);

        // then
        assertThat(httpHeaders.getHeaderValue(HttpHeaderName.CONTENT_LENGTH)).isEqualTo("12345");
    }

    @Test
    @DisplayName("추가된 헤더를 올바르게 조합하여 HTTP 메시지 형태로 반환한다.")
    void resolveHeadersMessageTest() {
        // given
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, "text/html");
        httpHeaders.putHeader(HttpHeaderName.CONTENT_LENGTH, "12345");

        // when & then
        String expectedMessage = "Content-Type: text/html\r\nContent-Length: 12345\r\n";
        assertThat(httpHeaders.resolveHeadersMessage()).isEqualTo(expectedMessage);
    }
}
