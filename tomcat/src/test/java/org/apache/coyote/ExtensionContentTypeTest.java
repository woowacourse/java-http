package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionContentTypeTest {

    @DisplayName("toConetentType 테스트")
    @Test
    void valueOf() {
        String contentType = ExtensionContentType.toContentType("html");

        assertThat(contentType).isEqualTo(ExtensionContentType.HTML.getContentType());
    }
}
