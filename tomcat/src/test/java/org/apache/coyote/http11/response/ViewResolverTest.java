package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @DisplayName("resource 경로에서 파일을 읽는다")
    @Test
    void findResponseFile() throws URISyntaxException, IOException {
        ViewResolver viewResolver = new ViewResolver();
        String filePath = "/login.html";

        URL resource = ClassLoader.getSystemClassLoader().getResource("static" + filePath);
        String expected = Files.readString(Path.of(resource.toURI()));
        String actual = viewResolver.findResponseFile(filePath);

        assertThat(actual).isEqualTo(expected);
    }
}
