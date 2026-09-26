package org.apache.catalina;

import org.apache.coyote.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("정적 리소스 조회")
class StaticResourcesTest {

    @Test
    @DisplayName("static 폴더의 파일을 MIME 타입과 내용으로 읽는다")
    void find() throws URISyntaxException, IOException {
        // when
        Optional<StaticResource> resource = StaticResources.find("/css/styles.css");

        // then
        assertThat(resource).isPresent();
        assertThat(resource.get().mimeType()).isEqualTo(MimeType.TEXT_CSS);
        assertThat(resource.get().content()).isEqualTo(readStatic("css/styles.css"));
    }

    @Test
    @DisplayName("존재하지 않는 파일이면 빈 값을 반환한다")
    void findMissingFile() throws URISyntaxException, IOException {
        assertThat(StaticResources.find("/nothing.html")).isEmpty();
    }

    @Test
    @DisplayName("디렉터리면 빈 값을 반환한다")
    void findDirectory() throws URISyntaxException, IOException {
        assertThat(StaticResources.find("/css")).isEmpty();
    }

    private byte[] readStatic(String name) throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("static/" + name);
        return Files.readAllBytes(Path.of(url.toURI()));
    }
}
