package org.apache.coyote.request.requestLine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.coyote.fixture.RequestLineFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    private static final String MAIN_PATH_WITH_NO_EXTENSION = "/index";
    private static final String MAIN_PATH = "/index.html";

    @Test
    @DisplayName("경로가 / 인 경우 true를 반환한다.")
    void isDefaultPath() {
        RequestLine requestLine = RequestLineFixture.DEFAULT_GET;

        assertTrue(requestLine.isDefaultPath());
    }

    @Test
    @DisplayName("같은 RequestMethod인지 확인한다.")
    void isSameMethod() {
        RequestLine requestLine = RequestLineFixture.MAIN_RESOURCE_GET;

        assertAll(
                () -> assertTrue(requestLine.isSameMethod(RequestMethod.GET)),
                () -> assertFalse(requestLine.isSameMethod(RequestMethod.POST))
        );
    }

    @Test
    @DisplayName("확장자가 없는 경로를 가져온다.")
    void getNoExtensionPath() {
        RequestLine requestLine = RequestLineFixture.MAIN_RESOURCE_GET;

        assertEquals(requestLine.getNoExtensionPath(), MAIN_PATH_WITH_NO_EXTENSION);
    }

    @Test
    @DisplayName("확장자가 있는 경로인지 확인한다.")
    void isResourcePath() {
        RequestLine requestLine = RequestLineFixture.MAIN_RESOURCE_GET;

        assertTrue(requestLine.isResourcePath());
    }

    @Test
    @DisplayName("같은 경로인지 확인한다.")
    void isSamePath() {
        RequestLine requestLine = RequestLineFixture.MAIN_RESOURCE_GET;

        assertTrue(requestLine.isSamePath(MAIN_PATH));
    }
}
