package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(value = {"/,text/html,html", "/index.html,text/html,html", "/login,text/html,html"})
    void from(final String uri, final String value, final String extension) {
        ContentType contentType = ContentType.from(uri);

        assertAll(
                () -> assertThat(contentType.getValue()).isEqualTo(value),
                () -> assertThat(contentType.getExtension()).isEqualTo(extension)
        );
    }
}
