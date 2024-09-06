package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class FileReaderTest {

    @Test
    @DisplayName("요청 파일을 읽을 수 있다.")
    void read() throws IOException {
        String actual = FileReader.read("static/index.html");

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(actual).isEqualTo(expected);
    }
}
