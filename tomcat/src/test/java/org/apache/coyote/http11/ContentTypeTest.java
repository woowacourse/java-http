package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void findContentType() {
        String contentType = ContentType.findContentType("/index.html");

        assertThat(contentType).isEqualTo("text/html");
    }
}