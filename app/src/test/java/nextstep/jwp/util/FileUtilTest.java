package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FileUtilTest {

    @DisplayName("uri path를 이용해 HTML 파일을 읽는다.")
    @ParameterizedTest
    @ValueSource(strings = {"/test", "/test.html"})
    void findHTMLFilePath(String input) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/test.html");
        String expected = new String(Files.readAllBytes(Paths.get(resource.getPath())));
        String actual = FileUtil.readStaticFileByUriPath(input);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("uri path를 이용해 JS, CSS 파일을 읽는다.")
    @ParameterizedTest
    @ValueSource(strings = {"/js/test.js", "/css/test.css"})
    void findStaticFilePath(String input) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + input);
        String expected = new String(Files.readAllBytes(Paths.get(resource.getPath())));
        String actual = FileUtil.readStaticFileByUriPath(input);

        assertThat(actual).isEqualTo(expected);
    }
}