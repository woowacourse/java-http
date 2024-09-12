package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.helper.FileReader;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @Test
    void should_readFileContentInResource_when_readResourceFile() throws URISyntaxException, IOException {
        // given
        FileReader fileReader = new FileReader();

        // when
        String fileContent = fileReader.readResourceFile("/default.html");

        // then
        String expected = "Hello world!";
        assertThat(fileContent).isEqualTo(expected);
    }

    @Test
    void should_throwException_when_readInvalidResourceFile() {
        // given
        FileReader fileReader = new FileReader();

        // when & then
        assertThatThrownBy(() -> fileReader.readResourceFile("/invalid.html"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 경로가 유효하지 않습니다.");
    }
}
