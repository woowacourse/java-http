package org.apache.coyote.http11.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void html타입에_맞는_ContentType을_반환한다() {
        ContentType matchingType = ContentType.findMatchingType("html");
        ContentType expected = ContentType.HTML;
        Assertions.assertThat(matchingType).isEqualTo(expected);
    }

    @Test
    void 슬래시는_HTML_ContentType을_반환한다() {
        ContentType matchingType = ContentType.findMatchingType("/");
        ContentType expected = ContentType.HTML;
        Assertions.assertThat(matchingType).isEqualTo(expected);
    }

    @Test
    void css타입에_맞는_ContentType을_반환한다() {
        ContentType matchingType = ContentType.findMatchingType("css");
        ContentType expected = ContentType.CSS;
        Assertions.assertThat(matchingType).isEqualTo(expected);
    }

    @Test
    void javascript타입에_맞는_ContentType을_반환한다() {
        ContentType matchingType = ContentType.findMatchingType("js");
        ContentType expected = ContentType.JS;
        Assertions.assertThat(matchingType).isEqualTo(expected);
    }
}
