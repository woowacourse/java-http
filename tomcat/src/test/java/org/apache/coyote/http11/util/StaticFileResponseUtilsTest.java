package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class StaticFileResponseUtilsTest {

    @Test
    void isExistFileTest_whenFileExist_returnTrue() {
        String filePath = "/test.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isTrue();
    }

    @Test
    void isExistFileTest_whenFileNotExist_returnFalse() {
        String filePath = "/not-exist.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isFalse();
    }

    @Test
    void readStaticFileTest() throws IOException {
        String filePath = "/test.html";
        URL resource = StaticFileResponseUtilsTest.class.getClassLoader()
                .getResource("static/test.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String actual = StaticFileResponseUtils.readStaticFile(filePath);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void readStaticFileTest_whenFileNotExist() {
        String filePath = "/not-exist.html";

        assertThatThrownBy(() -> StaticFileResponseUtils.readStaticFile(filePath))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("해당 파일이 존재하지 않습니다.");
    }

    @Test
    void getContentTypeTest_whenExtensionHtml() {
        String filePath = "/test.html";
        String expected = "text/html;charset=utf-8";

        String actual = StaticFileResponseUtils.getContentType(filePath);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getContentTypeTest_whenExtensionCss() {
        String filePath = "/test.css";
        String expected = "text/css;charset=utf-8";

        String actual = StaticFileResponseUtils.getContentType(filePath);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getContentTypeTest_whenNotExistExtension() {
        String filePath = "/test";

        assertThatThrownBy(() -> StaticFileResponseUtils.getContentType(filePath))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("파일의 확장자가 존재하지 않습니다.");
    }
}
