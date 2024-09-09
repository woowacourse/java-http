package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @Test
    void should_readDefaultFileContentInResource_when_readResourceFile() {
        // given & when
        String fileContent;
        try {
            fileContent = FileReader.readResourceFile();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        // then
        String expected = "Hello world!";
        assertThat(fileContent).isEqualTo(expected);
    }

    @Test
    void should_readFileContentInResource_when_readResourceFile() {
        // given & when
        String fileContent;
        try {
            fileContent = FileReader.readResourceFile("/default.html");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        // then
        String expected = "Hello world!";
        assertThat(fileContent).isEqualTo(expected);
    }

    @Test
    void should_returnDefaultFileContent_when_readInvalidResourceFile() {
        // given & when
        String fileContent;
        try {
            fileContent = FileReader.readResourceFile("/invalid.html");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        // then
        String expected = "Hello world!";
        assertThat(fileContent).isEqualTo(expected);
    }
}