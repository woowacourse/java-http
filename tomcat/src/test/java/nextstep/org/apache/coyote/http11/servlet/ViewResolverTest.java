package nextstep.org.apache.coyote.http11.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.servlet.ViewInfo;
import org.apache.coyote.http11.servlet.ViewResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ViewResolverTest {

    @DisplayName("정적 파일을 잘 렌더링하는지 확인한다.")
    @Test
    void render() throws IOException, URISyntaxException {
        // given
        final ViewResolver viewResolver = new ViewResolver("index.html");

        final URI uri = getClass().getClassLoader().getResource("static/index.html").toURI();
        final Path path = Paths.get(uri);
        final byte[] bytes = Files.readAllBytes(path);
        final String contentType = Files.probeContentType(path);

        // when
        final ViewInfo viewInfo = viewResolver.render();

        // then
        assertAll(
                () -> assertThat(viewInfo.getContentType()).isEqualTo(contentType),
                () -> assertThat(viewInfo.getContentLength()).isEqualTo(bytes.length),
                () -> assertThat(viewInfo.getViewContent()).isEqualTo(new String(bytes))
        );
    }
}
