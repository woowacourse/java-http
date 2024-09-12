package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class HttpContentTypeTest {

    @Test
    void 확장자로_알맞은_ContentType을_찾을_수_있다() {
        // given
        String htmlExtension = ".html";
        String cssExtension = ".css";
        String jsExtension = ".js";
        String icoExtension = ".ico";

        // when & then
        assertThat(HttpContentType.findByExtension(htmlExtension)).isEqualTo(HttpContentType.HTML);
        assertThat(HttpContentType.findByExtension(cssExtension)).isEqualTo(HttpContentType.CSS);
        assertThat(HttpContentType.findByExtension(jsExtension)).isEqualTo(HttpContentType.JAVASCRIPT);
        assertThat(HttpContentType.findByExtension(icoExtension)).isEqualTo(HttpContentType.ICO);
    }

    @Test
    void 지원하지_않는_확장자일_경우_예외가_발생한다() {
        // given
        String unsupportedExtension = ".exe";

        // when & then
        assertThatThrownBy(() -> HttpContentType.findByExtension(unsupportedExtension))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ContentType을 지원하지 않는 확장자입니다: " + unsupportedExtension);
    }

    @Test
    void ContentType에서_올바른_MimeType을_가져올_수_있다() {
        // given
        HttpContentType htmlType = HttpContentType.HTML;
        HttpContentType cssType = HttpContentType.CSS;
        HttpContentType jsType = HttpContentType.JAVASCRIPT;
        HttpContentType icoType = HttpContentType.ICO;

        // when & then
        assertThat(htmlType.getMimeType()).isEqualTo("text/html;charset=utf-8");
        assertThat(cssType.getMimeType()).isEqualTo("text/css;charset=utf-8");
        assertThat(jsType.getMimeType()).isEqualTo("application/javascript");
        assertThat(icoType.getMimeType()).isEqualTo("image/x-icon");
    }

}
