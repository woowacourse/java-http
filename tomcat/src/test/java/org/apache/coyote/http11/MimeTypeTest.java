package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MimeTypeTest {

    @DisplayName("확장자를 기준으로 MIME 타입을 판단하여 반환한다.")
    @ParameterizedTest
    @CsvSource({"index.html, text/html;charset=utf-8", "styles.css, text/css", "error.svg, image/svg+xml", "null, application/octet-stream"})
    void getMimeTypeByExtension(String extension, String expectedMimeType) {
        // when
        MimeType result = MimeType.getMimeType(extension);

        // then
        assertThat(result.getType()).isEqualTo(expectedMimeType);
    }
}
