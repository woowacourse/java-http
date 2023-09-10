package org.apache.coyote.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void toStringTest() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(HttpHeaders.CONTENT_LENGTH, "12");

        // when
        String result = headers.toString();

        // then
        assertThat(result).isEqualTo("Content-Type: text/html;charset=utf-8 " + "\r\n" + "Content-Length: 12 ");
    }
}
