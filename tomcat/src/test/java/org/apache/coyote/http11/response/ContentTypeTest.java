package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void findContentType() {
        String contentType = ContentType.findContentType("/index.html");

        assertThat(contentType).isEqualTo("text/html");
    }

    @Test
    void validateExist() {
        assertThat(ContentType.isExistExtension("/index.gg")).isFalse();
    }
}