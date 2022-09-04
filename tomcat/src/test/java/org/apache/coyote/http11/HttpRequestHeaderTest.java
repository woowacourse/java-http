package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @DisplayName(value = "Content-Length의 크기를 찾는다")
    @Test
    void findContentLength() {
        // given
        final int contentLength = 100;
        final List<String> requestHeaders = List.of("Content-Length: " + contentLength);

        // when
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);

        // then
        assertThat(httpRequestHeader.findContentLength()).isEqualTo(contentLength);
    }
}
