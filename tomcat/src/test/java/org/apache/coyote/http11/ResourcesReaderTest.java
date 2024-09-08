package org.apache.coyote.http11;

import org.apache.coyote.file.ClassLoaderContext;
import org.apache.coyote.file.Resource;
import org.apache.coyote.file.ResourcesReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ResourcesReaderTest {

    @Test
    @DisplayName("static 디렉토리를 기본으로 파일을 찾는다.")
    void read_static_path_default() throws IOException {
        final Resource resource = ResourcesReader.read("/main.html");


        final byte[] content = ClassLoaderContext.getResourceAsStream("static/main.html")
                .readAllBytes();

        assertThat(resource.getBytes()).isEqualTo(content);
    }
}

