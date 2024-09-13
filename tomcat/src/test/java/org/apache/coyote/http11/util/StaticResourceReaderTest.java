package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceReaderTest {

    @DisplayName("uri에 맞는 정적 리소스를 읽어온다.")
    @Test
    void read() throws IOException {
        String indexUri = "/index.html";
        final String read = StaticResourceReader.read(indexUri);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        Assertions.assertThat(read).isEqualTo(expected);
    }
}
