package org.apache.coyote.http11.common;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class FileReaderTest {

    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void 파일의_내용을_읽는다() throws IOException {
        String indexFile = FileReader.readFile("/index.html");

        String expected = extractFileData("/index.html");

        assertThat(indexFile).isEqualTo(expected);
    }

    private String extractFileData(String filePath) throws IOException {
        URL resource = classLoader.getResource("static" + filePath);
        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));
        return fileData;
    }
}
