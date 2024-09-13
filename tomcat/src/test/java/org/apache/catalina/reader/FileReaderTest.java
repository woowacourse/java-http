package org.apache.catalina.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @Nested
    @DisplayName("파일 읽기")
    class loadFileContent {

        @Test
        @DisplayName("성공 : 해당 경로의 파일 읽기 성공")
        void loadFileContentSuccess() throws IOException {
            String fileName = "/index.html";

            String actual = FileReader.loadFileContent(fileName);

            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String expected = new String(bytes);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 : 해당 경로의 파일이 존재하지 않을 경우 예외 발생")
        void loadFileContentFailByNotExistsFile() {
            String fileName = "/a.html";

            assertThatThrownBy(() -> FileReader.loadFileContent(fileName))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage(("'%s'이란 페이지를 찾을 수 없습니다.").formatted(fileName));
        }
    }
}
