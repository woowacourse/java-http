package org.apache.coyote.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MimeTypeTest {

    @ParameterizedTest
    @CsvSource({
            "/index.html, HTML",
            "/css/styles.css, CSS",
            "/js/scripts.js, JS",
            "/favicon.ico, ICO",
            "/assets/IMAGE.PNG, PNG",       // 확장자 대소문자를 구분하지 않는다
            "/login, DEFAULT",              // 확장자가 없다
            "/index.jsp, DEFAULT"           // 지원하지 않는 확장자
    })
    void 경로의_확장자로_MIME_타입을_결정한다(final String path, final MimeType expected) {
        assertThat(MimeType.fromPath(path)).isEqualTo(expected);
    }
}
