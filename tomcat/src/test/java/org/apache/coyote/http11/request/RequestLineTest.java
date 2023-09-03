package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("RequestLine을 생성한다.")
    void convert() {
        //given
        final String line = "GET /index.html HTTP/1.1";

        //when
        final RequestLine requestLine = RequestLine.convert(line);

        //then
        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestPath()).isEqualTo("/index.html");
        assertThat(requestLine.getRequestVersion()).isEqualTo("HTTP/1.1");
    }

}
