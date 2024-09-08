package org.apache.coyote.http11.finder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.coyote.http11.domain.ResourceFinder;
import org.apache.coyote.http11.request.domain.RequestPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceFinderTest {

    @Test
    @DisplayName("경로가 잘못되어 파일을 가져오지 못하는 경우 에러를 발생한다.")
    void find_WhenNoResourcePath() {
        assertThrows(IllegalArgumentException.class, () -> ResourceFinder.find(new RequestPath("")));
    }

    @Test
    @DisplayName("파일이 존재하는 경우 읽어온다.")
    void find_WhenResourcePath() {
        String body = ResourceFinder.find(new RequestPath("/index.html"));

        assertTrue(body.contains("<!DOCTYPE html>"));
    }
}
