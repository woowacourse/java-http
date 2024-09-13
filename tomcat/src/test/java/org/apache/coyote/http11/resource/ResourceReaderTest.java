package org.apache.coyote.http11.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceReaderTest {
    private final ResourceReader resourceReader = ResourceReader.getInstance();

    @ParameterizedTest
    @ValueSource(strings = {"index.html", "/index.html", "/assets/chart-area.js"})
    @DisplayName("정적 파일을 읽는다.")
    void loadResourceAsString() throws IOException {
        // given
        String fileName = "index.html";

        // when
        String content = resourceReader.loadResourceAsString(fileName);

        // then
        assertThat(content).isNotEmpty();
    }
}
