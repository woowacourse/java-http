package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.InvalidContentTypeException;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void from() {
        ContentType html = ContentType.from("html");

        assertThat(html).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    @Test
    void fromInvalidExtension() {
        assertThatThrownBy(() -> ContentType.from("lala"))
                .isInstanceOf(InvalidContentTypeException.class);
    }
}
