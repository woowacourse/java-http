package org.apache.coyote.http11.domain.body;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    @DisplayName("일치하는 Content Type을 가져올 수 있다.")
    void findContentType() {
        ContentType contentType = ContentType.findContentType("html");

        assertEquals(contentType, ContentType.HTML);
    }
}
