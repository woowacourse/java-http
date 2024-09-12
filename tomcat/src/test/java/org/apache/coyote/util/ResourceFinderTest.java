package org.apache.coyote.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceFinderTest {

    private static final String INDEX_FILE_PATH = "/index.html";
    private static final String NO_FILE_PATH = "/polla.html";

    @Test
    @DisplayName("존재하지 않은 URL인 경우 에러가 발생한다.")
    void findResourceByPath_WhenNotExistPath() {
        assertThrows(IllegalArgumentException.class,
                () -> ResourceFinder.findBy(NO_FILE_PATH));
    }

    @Test
    @DisplayName("존재하는 URL인 경우 파일을 찾아온다.")
    void findResourceByPath() {
        String resource = ResourceFinder.findBy(INDEX_FILE_PATH);

        assertTrue(resource.contains("<!DOCTYPE html>"));
    }
}
