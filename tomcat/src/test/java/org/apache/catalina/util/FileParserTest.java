package org.apache.catalina.util;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("파일이 존재하지 않으면 예외를 반환한다.")
    @Test
    void fileNotFoundTest1() {
        assertThatThrownBy(() -> FileParser.loadStaticResourceByFileName("nonexistent.txt"))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void shouldAcceptValidFileName() {
        assertThatCode(() -> FileParser.loadStaticResourceByFileName("index.html"))
                .doesNotThrowAnyException();
    }
}
