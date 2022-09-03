package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void 파일_요청인지_확인한다() {
        // given
        String uri = "/index.html";
        Path path = new Path(uri);

        // when & then
        assertThat(path.isFileRequest()).isTrue();
    }

    @Test
    void 파일명을_반환한다() {
        // given
        String uri = "/index.html";
        Path path = new Path(uri);

        // when & then
        assertThat(path.getFileName()).isEqualTo("index.html");
    }

    @Test
    void css_파일을_요청하면_ContentType으로_css를_응답한다() {
        // given
        String uri = "/css/styles.css";
        Path path = new Path(uri);

        // when & then
        assertThat(path.extractContentType()).isEqualTo("css");
    }

    @Test
    void css_파일_요청이_아니면_ContentType으로_html을_응답한다() {
        // given
        String uri = "/index.html";
        Path path = new Path(uri);

        // when & then
        assertThat(path.extractContentType()).isEqualTo("html");
    }
}
