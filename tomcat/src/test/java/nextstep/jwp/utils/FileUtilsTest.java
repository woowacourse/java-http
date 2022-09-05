package nextstep.jwp.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Objects;
import nextstep.jwp.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    @DisplayName("파일 경로를 받으면 파일 확장자를 반환한다.")
    void extractFileExtension_success() {
        String path = "/index.html";

        ContentType actual = FileUtils.extractFileExtension(path);

        ContentType expected = ContentType.TEXT_HTML;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("기본 파일 확장자로 plain을 반환한다.")
    void extractFileExtension_success_default() {
        String path = "/";

        ContentType actual = FileUtils.extractFileExtension(path);

        ContentType expected = ContentType.TEXT_PLAIN;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("파일 경로를 받으면 파일 내용을 반환한다.")
    void readFile(){
        final String fileName = "nextstep.txt";
        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(fileName));

        String actual = FileUtils.readFile(resource);

        String expected = "nextstep";
        assertThat(actual).isEqualTo(expected);
    }
}
