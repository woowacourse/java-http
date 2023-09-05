package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpVersion;
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
        assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

}
