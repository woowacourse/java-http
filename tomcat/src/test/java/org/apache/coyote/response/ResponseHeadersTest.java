package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    void ResponseHeaders를_조립한다() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.contentType("text/html");
        responseHeaders.contentLength(1024);

        // when
        StringBuilder builder = new StringBuilder();
        responseHeaders.assemble(builder);

        // then
        String expected = """
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                """;
        assertThat(builder.toString()).isEqualTo(expected);
    }
}
