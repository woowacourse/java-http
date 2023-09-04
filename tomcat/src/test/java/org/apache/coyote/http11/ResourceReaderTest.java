package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceReaderTest {

    @Test
    @DisplayName("해당하는 자원을 불러오는 테스트")
    void readResource() throws IOException {
        // given
        String fileName = "nextstep.txt";

        // when
        String actual = ResourceReader.readResource(fileName);

        // then
        URL resource = getClass().getClassLoader().getResource(fileName);
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }
}
