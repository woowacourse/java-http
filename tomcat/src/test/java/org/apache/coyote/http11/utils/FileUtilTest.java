package org.apache.coyote.http11.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileUtilTest {

    @DisplayName(value = "파일 찾기")
    @Test
    void findFile() {
        // given
        final String path = "/index.html";
        final File expected = new File(FileUtil.class
                .getClassLoader()
                .getResource("static/index.html")
                .getFile());

        // when
        final File file = FileUtil.findFile(path);

        // then
        assertThat(file).isEqualTo(expected);
    }

    @DisplayName(value = "content type 찾기")
    @CsvSource(value = {"/css/styles.css, text/css", "/index.html, text/html", "/js/scripts.js, text/javascript",
            "/favicon.ico, image/x-icon"})
    @ParameterizedTest(name = "value = {0}, expected = {1}")
    void findContentType(final String path, final String expected) {
        // given
        final File file = new File(FileUtil.class
                .getClassLoader()
                .getResource("static" + path)
                .getFile());

        // when
        final String contentType = FileUtil.findContentType(file);

        // then
        assertThat(contentType).isEqualTo(expected);
    }

    @DisplayName(value = "파일 내용 읽어오기")
    @Test
    void generateFile() {
        // given
        final File file = new File(FileUtil.class
                .getClassLoader()
                .getResource("static/500.html")
                .getFile());

        // when
        final String fileContent = FileUtil.generateFile(file);

        // then
        assertThat(fileContent).contains("<p class=\"lead\">Internal Server Error</p>");
    }
}
