package org.apache.coyote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MimeTypeTest {

    @Test
    @DisplayName("파일 확장자에 맞는 Content-Type을 반환한다.")
    void findContentTypeByExtension() {
        assertThat(MimeType.from("/index.html")).isEqualTo("text/html;charset=utf-8");
        assertThat(MimeType.from("/css/styles.css")).isEqualTo("text/css;charset=utf-8");
        assertThat(MimeType.from("/js/scripts.js")).isEqualTo("application/javascript;charset=utf-8");
        assertThat(MimeType.from("/assets/img/error-404-monochrome.svg")).isEqualTo("image/svg+xml");
    }

    @Test
    @DisplayName("알 수 없는 확장자는 html Content-Type으로 반환한다.")
    void defaultContentType() {
        assertThat(MimeType.from("/unknown")).isEqualTo("text/html;charset=utf-8");
    }
}
