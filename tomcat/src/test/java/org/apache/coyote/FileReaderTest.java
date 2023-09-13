package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.FileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @Test
    @DisplayName("static 디렉터리에 위치한 정적 파일을 읽어온다.")
    void readStaticFile_success() {
        // given
        final String name = "/index.html";

        // when
        final String fileContent = FileReader.readStaticFile(name);

        // then
        assertThat(fileContent).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 파일을 요청하면 404.html 을 읽는다.")
    void readStaticFile_noUrlFound() {
        // given
        final String name = "/hello.html";

        // when
        final String fileContent = FileReader.readStaticFile(name);

        // then
        final String expected = FileReader.readStaticFile("/404.html");
        assertThat(fileContent).isEqualTo(expected);
    }
}
