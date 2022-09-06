package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ContentTypeTest {

    @DisplayName("정적 파일에 맞는 contentType을 반환한다.")
    @ParameterizedTest
    @CsvSource({"/index.html, text/html", "/scripts.js, application/js", "/styles.css, text/css"})
    void returnContentType(String uri, String expected) {
        // when
        ContentType contentType = ContentType.from(uri);
        // then
        assertThat(contentType.value()).isEqualTo(expected);
    }
}
