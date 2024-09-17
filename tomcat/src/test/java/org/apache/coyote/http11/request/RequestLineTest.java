package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("객체 생성 시 문자열을 HTTP 형식에 맞게 파싱하여 필드에 저장한다.")
    @Test
    void should_parseComponents_when_construct() {
        // given
        String input = "GET /path HTTP/1.1";

        // when
        RequestLine requestLine = new RequestLine(input);

        // then
        assertThat(requestLine.hasPath("/path")).isTrue();
        assertThat(requestLine.hasGetMethod()).isTrue();
    }
}
