package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @DisplayName("content-type 값을 반환한다.")
    @ParameterizedTest
    @CsvSource({"/index.js, Content-Type: text/js;charset=utf-8 ",
            "/index.html, Content-Type: text/html;charset=utf-8 ",
            "/index.css, Content-Type: text/css;charset=utf-8 ",
            "/, Content-Type: text/plain;charset=utf-8 ",
            "/favicon.ico, Content-Type: text/plain;charset=utf-8 ",})
    void getValue(String fileSource, String expected) {
        ContentType contentType = ContentType.from(fileSource);

        String actual = contentType.getValue();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("fileSource로 content-type을 식별할 수 없으면 예외를 발생시킨다.")
    @Test
    void from_ContentTypeNotFoundException() {
        String invalidFileSource = "index.abc";

        assertThatThrownBy(() -> ContentType.from(invalidFileSource))
                .isInstanceOf(ContentTypeNotFoundException.class);
    }

}