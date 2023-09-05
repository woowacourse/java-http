package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(value = {"index.html,text/html", "abc.js,application/js", "bbb.css,text/css"})
    void 파일_확장자와_일치하는_ContentType_객체를_반환한다(String filename, String expected) {
        ContentType contentType = ContentType.from(filename);

        assertThat(contentType.getValue()).isEqualTo(expected);
    }
}
