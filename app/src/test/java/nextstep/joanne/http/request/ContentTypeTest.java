package nextstep.joanne.http.request;

import nextstep.joanne.exception.PageNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentTypeTest {

    @Test
    void resolveCSS() {
        String contentType = ContentType.resolve("css/style.css");
        assertThat(contentType).isEqualTo("text/css");
    }

    @Test
    void resolveHTML() {
        String contentType = ContentType.resolve("main/index.html");
        assertThat(contentType).isEqualTo("text/html");
    }

    @Test
    void resolveJS() {
        String contentType = ContentType.resolve("main/index.js");
        assertThat(contentType).isEqualTo("application/javascript");
    }

    @Test
    void resolveFailed() {
        assertThatThrownBy(() -> ContentType.resolve("main/index.babo"))
                .isInstanceOf(PageNotFoundException.class);
    }
}