package nextstep.jwp;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceReaderTest {

    private final ResourceReader resourceReader = new ResourceReader();

    @Test
    void 자원을_찾으면_해당_값을_읽어_문자열로_반환한다() throws NotFoundException, URISyntaxException, IOException {
        // given, when
        final String content = resourceReader.read("/index.html");
        // then
        assertThat(content).isNotNull();
    }

    @Test
    void 자원을_찾지_못하면_예외를_발생한다() {
        // given, when. then
        assertThatThrownBy(() -> resourceReader.read("/notfound.html"))
                .isInstanceOf(NotFoundException.class);
    }

    private String readContent(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

}
