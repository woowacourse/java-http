package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileReaderTest {

    @ParameterizedTest
    @ValueSource(strings = {"/login.html", "/register.html", "/index.html"})
    void 파일_내용을_읽는다(String path) throws IOException {
        String content = FileReader.read(path);

        assertThat(content).isNotBlank();
    }
}
