package org.apache.coyote.request.requestLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestPathTest {

    public static final String MAIN_PATH = "/index.html";
    public static final String MAIN_PATH_WITH_NO_EXTENSION = "/index";
    public static final String MAIN_PATH_WITH_QUERY = "/index.html?name=polla";

    @Test
    @DisplayName("경로가 Null인 경우 에러를 발생한다.")
    void getPath() {
        assertThrows(NullPointerException.class, () -> new RequestPath(null));
    }

    @Test
    @DisplayName("경로가 / 일 경우 true를 반환한다.")
    void isDefaultPath() {
        RequestPath requestPath = new RequestPath("/");

        assertTrue(requestPath.isDefaultPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/index"})
    @DisplayName("확장자가 없는 경로를 가져온다.")
    void getNoExtensionPath(String path) {
        RequestPath requestPath = new RequestPath(path);

        assertEquals(requestPath.getNoExtensionPath(), MAIN_PATH_WITH_NO_EXTENSION);
    }

    @Test
    @DisplayName("확장자가 있는 경우 true를 반환한다.")
    void hasExtension() {
        RequestPath requestPath = new RequestPath(MAIN_PATH);

        assertTrue(requestPath.hasExtension());
    }

    @Test
    @DisplayName("파라미터를 제외한 경로가 같을 경우 true를 반환한다.")
    void hasSamePath() {
        RequestPath requestPath = new RequestPath(MAIN_PATH_WITH_QUERY);

        assertTrue(requestPath.hasSamePath(MAIN_PATH));
    }
}
