package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersStringifierTest {

    @Test
    @DisplayName("HttpHeaders 객체를 문자열로 변환한다.")
    void stringifyTest() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "text/plain");
        headers.setHeader(HttpHeaderName.CONTENT_LENGTH, "1234");
        headers.setHeader(HttpHeaderName.CONNECTION, "keep-alive");

        // when
        List<String> headerLines = HttpHeadersStringifier.stringify(headers);

        // then
        assertThat(headerLines).containsExactlyInAnyOrder(
                "Content-Type: text/plain",
                "Content-Length: 1234",
                "Connection: keep-alive"
        );
    }
}
