package nextstep.org.apache.coyote.http11.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @DisplayName("contentType을 찾을 수 있다")
    @ParameterizedTest
    @CsvSource({"/, HTML", "/html.html, HTML", "/style.css, CSS", "/javascript.js, JAVASCRIPT",
            "/favicon.ico, FAVICON"})
    void findContentType(final String path, final ContentType expected) {
        assertThat(ContentType.from(path)).isEqualTo(expected);
    }
}
