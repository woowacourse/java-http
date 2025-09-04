package org.apache.catalina.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FileParserTest {

    @Test
    void shouldThrowExceptionWhenPathContainsDoubleDotSlash() {
        assertThatThrownBy(() -> FileParser.loadStaticResourceByFileName("../../../etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path traversal attempt detected");
    }

    @Test
    void shouldThrowExceptionWhenPathContainsDoubleDotBackslash() {
        assertThatThrownBy(() -> FileParser.loadStaticResourceByFileName("..\\..\\windows\\system32\\config\\sam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path traversal attempt detected");
    }

    @Test
    void shouldThrowExceptionWhenPathContainsDoubleDot() {
        assertThatThrownBy(() -> FileParser.loadStaticResourceByFileName("..config"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid path format");
    }

    @Test
    void shouldReturnFileNotFoundForNonExistentFile() throws Exception {
        byte[] result = FileParser.loadStaticResourceByFileName("nonexistent.txt");
        assertThat(new String(result)).isEqualTo("File not found");
    }

    @Test
    void shouldAcceptValidFileName() {
        assertThatCode(() -> FileParser.loadStaticResourceByFileName("index.html"))
                .doesNotThrowAnyException();
    }
}
