package org.apache.coyote.http11.response.headers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentLengthTest {

    @Test
    void createWithBodyString() {
        // given
        String bodyString = "hello world";
        // when
        ContentLength contentLength = ContentLength.fromBody(bodyString);
        // then
        assertThat(contentLength.getAsString()).isEqualTo("Content-Length: 11");
    }
}
