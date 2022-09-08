package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    void createResponseHeaders() {
        // given
        String bodyString = "hello world";
        // when
        ResponseHeaders headers = ResponseHeaders.empty().update(bodyString);
        // then
        assertThat(headers).isNotNull();
    }

    @Test
    void getAsString() {
        // given
        String bodyString = "hello world";
        ResponseHeaders headers = ResponseHeaders.empty().update(bodyString);

        // when
        String headersString = headers.getAsString();

        // then
        assertThat(headersString).isEqualTo("Content-Length: 11");
    }
}
