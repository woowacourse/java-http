package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.ContentTypeNotSupportedException;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void fromCss() {
        String css = "css";
        assertThat(ContentType.from(css)).isEqualTo(ContentType.TEXT_CSS);
    }

    @Test
    void fromJs() {
        String js = "js";
        assertThat(ContentType.from(js)).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
    }

    @Test
    void fromHtml() {
        String html = "html";
        assertThat(ContentType.from(html)).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    @Test
    void fromException() {
        String jpg = "jpg";
        assertThatThrownBy(
                () -> ContentType.from(jpg)
        ).isInstanceOf(ContentTypeNotSupportedException.class);
    }

}
