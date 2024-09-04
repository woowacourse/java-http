package org.apache.coyote.http11.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceFinderTest {

    @Test
    @DisplayName("존재하는 자원 경로가 없는 경우 Default를 응답한다.")
    void find_WhenDefaultResourcePath() {
        String body = ResourceFinder.find("/");

        assertEquals(ResourceFinder.DEFAULT_BODY, body);
    }

    @Test
    @DisplayName("경로가 잘못되어 파일을 가져오지 못하는 경우 에러를 발생한다.")
    void find_WhenNoResourcePath() {
        assertThrows(IllegalArgumentException.class, () -> ResourceFinder.find(""));
    }

    @Test
    @DisplayName("파일이 존재하는 경우 읽어온다.")
    void find_WhenResourcePath() {
        String body = ResourceFinder.find("/index.html");

        assertTrue(body.contains("<!DOCTYPE html>"));
    }
}
