package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FilePathTest {

    @DisplayName("FilePath 생성")
    @ParameterizedTest(name = "value = {0}, expected = {1}")
    @CsvSource(value = {"/,DEFAULT_PAGE", "/index.html,INDEX_PAGE", "/css/styles.css,INDEX_CSS",
            "/js/scripts.js,INDEX_JS"})
    void of(String url, FilePath expected) {
        // given & when & then
        assertThat(FilePath.of(url)).isEqualTo(expected);
    }

    @DisplayName("유효하지 않은 파일 경로")
    @Test
    void isNotFilePath() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.isNotFilePath("/login")).isTrue();
    }

    @DisplayName("유효한 파일 경로")
    @Test
    void isFilePath() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.isNotFilePath("/login.html")).isFalse();
    }

    @DisplayName("파일 확장자 추출")
    @Test
    void findFileExtension() {
        // given
        FilePath filePath = FilePath.LOGIN_PAGE;

        // when & then
        assertThat(filePath.findFileExtension()).isEqualTo("html");
    }
}
