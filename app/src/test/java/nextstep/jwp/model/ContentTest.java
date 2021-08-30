package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTest {

    @DisplayName("Content의 내용을 반환한다.")
    @Test
    void getContentValue() throws IOException {
        // given
        URL url = ClassLoader.getSystemResource("static/index.html");
        File file = new File(url.getFile());

        String expectContentValue = new String(Files.readAllBytes(file.toPath()));

        // when
        Content content = Content.readFile(file);
        String contentValue = content.getValue();

        // then
        assertThat(contentValue).isEqualTo(expectContentValue);
    }

    @DisplayName("Content 내용의 길이를 반환한다.")
    @Test
    void getContentLength() throws IOException {
        // given
        URL url = ClassLoader.getSystemResource("static/index.html");
        File file = new File(url.getFile());

        byte[] expectContentValue = Files.readAllBytes(file.toPath());
        int expectContentLength = expectContentValue.length;

        // when
        Content content = Content.readFile(file);
        int contentLength = content.getLength();

        // then
        assertThat(contentLength).isEqualTo(expectContentLength);
    }
}