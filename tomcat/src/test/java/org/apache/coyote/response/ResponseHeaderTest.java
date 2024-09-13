package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HeaderName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {
    @Test
    void 헤더_요소를_추가한다() {
        // given
        ResponseHeader responseHeader = new ResponseHeader();

        // when
        responseHeader.addHeader(HeaderName.CONTENT_TYPE, "text/html");

        // then
        assertThat(responseHeader.getResponse()).isEqualTo("Content-Type: text/html\r\n");
    }
}
