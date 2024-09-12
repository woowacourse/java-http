package org.apache.coyote.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilePathParserTest {

    @Test
    @DisplayName("리소스 경로 반환: 확장자가 없는 경우, /static 경로 아래의 html 리소스 경로 반환")
    void findResourcePath_When_Extension_NotExist() {
        assertEquals("static/404.html", FilePathParser.findResourcePath("/404.html"));
    }

    @Test
    @DisplayName("리소스 경로 반환: 확장자가 있는 경우 기본적으로 /static 경로에 있는 리소스 경로 반환")
    void findResourcePath_Default() {
        assertEquals("static/404.html", FilePathParser.findResourcePath("/404.html"));
    }

    @Test
    @DisplayName("리소스 경로 반환: 확장자가 있는 경우 확장자에 따라 다른 경로에 있는 리소스를 반환")
    void findResourcePath_When_Different_Extension_Resource()  {
        assertEquals("static/css/styles.css", FilePathParser.findResourcePath("/css/styles.css"));
    }
}
