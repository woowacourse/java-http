package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceTest {

    @DisplayName("Content, ContentType, ContentLength를 String으로 반환한다.")
    @Test
    void getter() throws IOException {
        // given
        URL url = ClassLoader.getSystemResource("static/index.html");
        File file = new File(url.getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());

        String expectContent = new String(bytes);
        String expectContentType = "text/html; charset=UTF-8";
        String expectContentLength = String.valueOf(bytes.length);

        // when
        StaticResource staticResource = StaticResource.from(file);

        String content = staticResource.getContent();
        String contentType = staticResource.getContentType();
        String contentLength = staticResource.getContentLength();

        // then
        assertThat(content).isEqualTo(expectContent);
        assertThat(contentType).isEqualTo(expectContentType);
        assertThat(contentLength).isEqualTo(expectContentLength);
    }
}