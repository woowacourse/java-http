package org.apache.coyote.http11.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class FileReaderTest {

    @DisplayName("파일 경로를 기반으로 파일 내용을 읽어 반환한다.")
    @Test
    void should_readFileContentInResource_when_readResourceFile() throws URISyntaxException, IOException {
        // given
        FileReader fileReader = FileReader.getInstance();

        // when
        String fileContent = fileReader.readResourceFile("/default.html");

        // then
        String expected = "Hello world!";
        assertThat(fileContent).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 파일에 대해 읽기를 요청한 경우 예외를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"/invalid.html", "invalid.html", "invalid", ".html"})
    void should_throwException_when_readInvalidResourceFile(String input) {
        // given
        FileReader fileReader = FileReader.getInstance();

        // when & then
        assertThatThrownBy(() -> fileReader.readResourceFile(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 파일입니다.");
    }
}
