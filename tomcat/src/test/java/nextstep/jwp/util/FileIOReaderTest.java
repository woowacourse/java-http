package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.util.FileIOReader.ViewResolver;
import org.junit.jupiter.api.Test;

class FileIOReaderTest {

    @Test
    void 단순_url_파싱() {
        String resolve = ViewResolver.resolve("/index");

        assertThat(resolve).isEqualTo("static/index.html");
    }

    @Test
    void html_붙은_url_파싱() {
        String resolve = ViewResolver.resolve("/index.html");

        assertThat(resolve).isEqualTo("static/index.html");
    }

    @Test
    void css_파싱() {
        String resolve = ViewResolver.resolve("/style.css");

        assertThat(resolve).isEqualTo("static/style.css");
    }

}
