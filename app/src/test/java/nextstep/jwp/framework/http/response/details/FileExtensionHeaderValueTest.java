package nextstep.jwp.framework.http.response.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileExtensionHeaderValueTest {

    @DisplayName("파일 확장자에 따른 헤더값을 얻을 수 있다.")
    @Test
    void extensionHeaderValue() {
        final FileExtensionHeaderValue HTML = FileExtensionHeaderValue.of("html");
        final FileExtensionHeaderValue CSS = FileExtensionHeaderValue.of("css");
        final FileExtensionHeaderValue JS = FileExtensionHeaderValue.of("js");
        final FileExtensionHeaderValue SVG = FileExtensionHeaderValue.of("svg");

        assertThat(HTML.getHeaderValue()).isEqualTo("text/html;charset=utf-8");
        assertThat(CSS.getHeaderValue()).isEqualTo("text/css");
        assertThat(JS.getHeaderValue()).isEqualTo("application/javascript");
        assertThat(SVG.getHeaderValue()).isEqualTo("image/svg+xml");
    }
}
