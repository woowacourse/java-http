package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Http11ContentTypeFinderTest {

    @Test
    @DisplayName("가능한 content-type인 경우 잘 파싱하는지 확인")
    void find() {
        Path path = Path.of("index.html");
        Http11ContentTypeFinder contentTypeFinder = new Http11ContentTypeFinder();

        String rawContentTypeString = contentTypeFinder.find(path);
        assertThat(rawContentTypeString).isEqualTo("text/html");
    }

    @ParameterizedTest
    @ValueSource(strings = {"inedex.invalid", "index"})
    @DisplayName("content-type을 추정할 수 없는 경우 text/html로 판단하는지 확인")
    void findInvalidPath(String fileName) {
        Path path = Path.of(fileName);
        Http11ContentTypeFinder contentTypeFinder = new Http11ContentTypeFinder();

        String rawContentTypeString = contentTypeFinder.find(path);
        assertThat(rawContentTypeString).isEqualTo("text/html");
    }
}
