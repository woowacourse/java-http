package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @Test
    void findContentType_메서드는_기준이_되는_문자열을_전달하면_그에_맞는_ContentType을_반환한다() {
        final String resourceName = "index.html";

        final ContentType actual = ContentType.findContentType(resourceName);

        assertThat(actual).isEqualTo(ContentType.TEXT_HTML);
    }
}
