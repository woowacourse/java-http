package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource({
            "html, TEXT_HTML",
            "css, TEXT_CSS",
            "js, TEXT_JAVASCRIPT",
            "txt, TEXT_PLAIN",
            "svg, IMAGE_SVG_XML"
    })
    void 파일확장자로_컨텐츠타입을_결정한다(String fileExtension, ContentType expected) {

        ContentType actual = ContentType.of(fileExtension);

        assertThat(actual).isEqualTo(expected);
    }

}