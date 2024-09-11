package org.apache.coyote.http11;

import org.apache.coyote.file.ClassLoaderContext;
import org.apache.coyote.file.Resource;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.path.Path;
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

    @Test
    @DisplayName("맨 앞에 슬래시(/) 가 없으면, 추가해서 찾는다.")
    void read_path_with_slash_prefix() throws IOException {
        final Resource resource = ResourcesReader.read(Path.from("main.html"));


        final byte[] content = ClassLoaderContext.getResourceAsStream("static/main.html")
                .readAllBytes();

        assertThat(resource.getBytes()).isEqualTo(content);
    }
}

