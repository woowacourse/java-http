package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("확장자가 존재하지 않을 때, TEXT_HTML 반환")
    @Test
    void urlIsDirectory_returnTextHtml() {
        String url = "http://localhost:8080/hello";

        ContentType result = ContentType.findByExtension(url);

        assertThat(result).isEqualTo(ContentType.TEXT_HTML);
    }

    @DisplayName("확장자가 css일 때, TEXT_CSS 반환")
    @Test
    void endsWithCss_returnTextCss() {
        String url = "http://localhost:8080/style.css";

        ContentType result = ContentType.findByExtension(url);

        assertThat(result).isEqualTo(ContentType.TEXT_CSS);
    }

    @DisplayName("확장자가 js일 때, TEXT_JAVASCRIPT 반환")
    @Test
    void endsWithJs_returnTextJavascript() {
        String url = "http://localhost:8080/charts.js";

        ContentType result = ContentType.findByExtension(url);

        assertThat(result).isEqualTo(ContentType.TEXT_JAVASCRIPT);
    }
}
