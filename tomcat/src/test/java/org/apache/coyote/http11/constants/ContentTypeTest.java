package org.apache.coyote.http11.constants;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("content type에 utf8을 붙여 반환한다.")
    @Test
    void getContentTypeUtf8() {
        assertThat(ContentType.HTML.getContentTypeUtf8()).isEqualTo("text/html; charset=utf-8");
    }

    @DisplayName("파일 확장자로 content type을 찾아 반환한다.")
    @Test
    void toContentType() {
        String fileExtension = "html";
        final ContentType contentType = ContentType.toContentType(fileExtension);
        assertThat(contentType).isEqualTo(ContentType.HTML);
    }

    @DisplayName("uri의 파일 확장자가 유효한지 확인한다.")
    @Test
    void isValidType() {
        String validUri = "/test.css";
        String inValidUri = "/test.abc";

        assertAll(
                () -> assertThat(ContentType.isValidType(validUri)).isTrue(),
                () -> assertThat(ContentType.isValidType(inValidUri)).isFalse()
        );
    }
}
