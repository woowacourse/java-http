package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FilePathTest {

    @ParameterizedTest
    @CsvSource(value = {"/,DEFAULT_PAGE", "/index.html,INDEX_PAGE", "/css/styles.css,INDEX_CSS",
            "/js/scripts.js,INDEX_JS"})
    void of(String url, FilePath expected) {
        // given & when & then
        assertThat(FilePath.of(url)).isEqualTo(expected);
    }

    @Test
    void isNotFilePath() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.isNotFilePath("/login")).isTrue();
    }

    @Test
    void isFilePath() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.isNotFilePath("/login.html")).isFalse();
    }

    @Test
    void findFileExtension() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.findFileExtension()).isEqualTo("html");
    }
}
