package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

class FileReaderTest {

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
    void should_throwException_when_readInvalidResourceFile() {
        assertThatThrownBy(() -> FileReader.readResourceFile("/invalid.html"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 경로가 유효하지 않습니다.");
    }
}
