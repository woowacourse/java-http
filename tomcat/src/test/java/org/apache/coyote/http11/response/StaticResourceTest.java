package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StaticResourceTest {

    @Test
    void 파일_경로로_파일을_읽어온다() throws IOException {
        // given
        StaticResource staticResource = StaticResource.of("/index.html");

        // when
        String content = staticResource.fileToString();

        // then
        assertThat(content).contains("<!DOCTYPE html>");
    }

    @Test
    void 파일_경로로_파일_확장자를_읽어온다() throws IOException {
        // given
        StaticResource staticResource = StaticResource.of("/index.html");

        // when
        String extension = staticResource.getFileExtension();

        // then
        assertThat(extension).isEqualTo("html");
    }

}
