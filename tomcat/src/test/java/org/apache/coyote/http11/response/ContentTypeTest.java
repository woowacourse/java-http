package org.apache.coyote.http11.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    void 경로의_확장자로_형식을_찾는다() {
        assertThat(ContentType.from("/index.html")).isEqualTo(ContentType.HTML);
        assertThat(ContentType.from("/css/styles.css")).isEqualTo(ContentType.CSS);
        assertThat(ContentType.from("/js/scripts.js")).isEqualTo(ContentType.JAVASCRIPT);
        assertThat(ContentType.from("/assets/img/error-404-monochrome.svg")).isEqualTo(ContentType.SVG);
    }

    @Test
    void 모르는_확장자면_HTML이다() {
        assertThat(ContentType.from("/favicon.ico")).isEqualTo(ContentType.HTML);
    }

    @Test
    void 확장자가_없으면_HTML이다() {
        assertThat(ContentType.from("/login")).isEqualTo(ContentType.HTML);
    }
}
