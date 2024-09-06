package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class StaticFileUtilsTest {

    @Test
    void isExistStaticFileTest_whenFileExist_returnTrue() {
        String filePath = "/test.html";

        boolean actual = StaticFileUtils.isExistStaticFile(filePath);

        assertThat(actual).isTrue();
    }

    @Test
    void isExistStaticFileTest_whenFileNotExist_returnFalse() {
        String filePath = "/not-exist.html";

        boolean actual = StaticFileUtils.isExistStaticFile(filePath);

        assertThat(actual).isFalse();
    }

    @Test
    void readStaticFileTest_whenFileExist() throws IOException {
        String filePath = "/test.html";
        String expected = loadResourceFile("static/test.html");

        String actual = StaticFileUtils.readStaticFile(filePath);

        assertThat(actual).isEqualTo(expected);
    }

    private String loadResourceFile(String resourcePath) throws IOException {
        URL resource = StaticFileUtilsTest.class.getClassLoader()
                .getResource(resourcePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    @Test
    void readStaticFileTest_whenFileNotExist_throwException() {
        String filePath = "/not-exist.html";

        assertThatThrownBy(() -> StaticFileUtils.readStaticFile(filePath))
                .isExactlyInstanceOf(UncheckedServletException.class)
                .hasMessage("해당 파일이 존재하지 않습니다.");
    }
}
