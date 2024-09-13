package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceReaderTest {

    @DisplayName("resource path로 파일 내용을 읽는다.")
    @Test
    void read() throws IOException {
        String file = getClass().getClassLoader().getResource("static/index.html").getFile();
        String expect = new String(Files.readAllBytes(new File(file).toPath()));

        String actual = ResourceReader.read("/index.html");

        assertThat(actual).isEqualTo(expect);
    }
}
