package org.apache.coyote.http11.response.headers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void createContentType_textPlain() {
        // given
        String path = "/";
        // when
        ContentType contentType = ContentType.findWithExtension(path);
        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_PLAIN);
    }

    @Test
    void createContentType_textHtml() {
        // given
        String path = "/hello.html";
        // when
        ContentType contentType = ContentType.findWithExtension(path);
        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML);
    }

    @Test
    void createContentType_textCss() {
        // given
        String path = "/hello.css";
        // when
        ContentType contentType = ContentType.findWithExtension(path);
        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS);
    }

    @Test
    void createContentType_applicationJavascript() {
        // given
        String path = "/hello.js";
        // when
        ContentType contentType = ContentType.findWithExtension(path);
        // then
        assertThat(contentType).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
    }
}
