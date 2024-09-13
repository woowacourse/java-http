package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersParserTest {

    @Test
    @DisplayName("헤더 필드를 파싱한다.")
    void parseToHeadersTest() {
        // given
        List<String> headerLines = List.of(
                "Content-Type: text/plain",
                "Content-Length: 1234",
                "Connection: keep-alive"
        );

        // when
        HttpHeaders headers = HttpHeadersParser.parseToHeaders(headerLines);

        // then
        assertThat(headers.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE)).contains("text/plain");
        assertThat(headers.getFieldByHeaderName(HttpHeaderName.CONTENT_LENGTH)).contains("1234");
        assertThat(headers.getFieldByHeaderName(HttpHeaderName.CONNECTION)).contains("keep-alive");
    }
}
