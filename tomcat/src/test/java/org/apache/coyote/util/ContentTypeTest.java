package org.apache.coyote.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    private static final String MAIN_RESOURCE_PATH = "static/index.html";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String NO_EXTENSION_PATH = "login";
    private static final String NOT_EXIST_CONTENT_TYPE = "text/polla";
    private static final String HAS_EXTENSION_PATH = "login.html";
    private static final String HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Test
    @DisplayName("경로에 존재하는 확장자를 가져온다.")
    void findContentTypeByPath() {
        ContentType contentType = ContentType.findContentTypeByPath(MAIN_RESOURCE_PATH);

        assertEquals(contentType, ContentType.HTML);
    }

    @Test
    @DisplayName("헤더의 콘텐츠 타입에 일치하는 경우 가져온다.")
    void findContentTypeByHeader() {
        ContentType contentType = ContentType.findContentTypeByHeader(CSS_CONTENT_TYPE);

        assertEquals(contentType, ContentType.CSS);
    }

    @Test
    @DisplayName("경로에 확장자가 없는 경우 에러가 발생한다.")
    void findContentTypeByNoExtensionPath() {
        assertThrows(IllegalArgumentException.class,
                () -> ContentType.findContentTypeByPath(NO_EXTENSION_PATH));
    }

    @Test
    @DisplayName("지원하는 콘텐츠 타입이 아닌 경우 에러가 발생한다.")
    void findContentTypeByNotAvailableHeader() {
        assertThrows(IllegalArgumentException.class,
                () -> ContentType.findContentTypeByHeader(NOT_EXIST_CONTENT_TYPE));
    }

    @Test
    @DisplayName("확장자가 존재하는 경우 true를 반환한다.")
    void hasExtension() {
        assertTrue(ContentType.hasExtension(HAS_EXTENSION_PATH));
    }

    @Test
    @DisplayName("컨텐츠 타입을 가져오는 경우 인코딩 타입을 붙여서 가져온다")
    void getContentType_WithEncodingType() {
        String expect = HTML_CHARSET_UTF_8;

        assertEquals(ContentType.HTML.getContentType(), expect);
    }
}
