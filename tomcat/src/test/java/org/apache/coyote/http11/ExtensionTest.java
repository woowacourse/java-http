package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.Extension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionTest {

    @DisplayName("확장자에 .html을 포함하면 text/html contentType 이다.")
    @Test
    void from() {
        final String resource = "index.html";
        final String expected = "text/html";

        Extension extension = Extension.from(resource);

        assertThat(extension.getContentType()).isEqualTo(expected);
    }

    @DisplayName("확장자가 없으면 text/html contentType 이다.")
    @Test
    void from_null() {
        final String expected = "text/html";

        Extension extension = Extension.from("index");

        assertThat(extension.getContentType()).isEqualTo(expected);
    }

}
