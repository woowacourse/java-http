package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ResponseHeadersTest {

    @Test
    void addHeader_success() {
        // given
        final ResponseHeaders headers = new ResponseHeaders();

        // when
        headers.addHeader(
                "Content-Type",
                "text/html;charset=utf-8"
        );

        // then
        assertThat(headers.getHeader("Content-Type"))
                .isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void createHeaderLines_success() {
        // given
        final ResponseHeaders headers = new ResponseHeaders();

        headers.addHeader("Content-Type", "text/html");
        headers.addHeader("Content-Length", "12");

        // when
        final String result = headers.toResponseHeaders();

        // then
        assertThat(result)
                .contains("content-type: text/html")
                .contains("content-length: 12");
    }
}
