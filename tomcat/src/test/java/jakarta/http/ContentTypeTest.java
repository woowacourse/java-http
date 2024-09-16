package jakarta.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(
            value = {
                    "index.html:text/html",
                    "style.css:text/css",
                    "sample.js:text/javascript",
                    "image.svg:image/svg+xml",
                    "hi.unknown:text/plain"
            },
            delimiter = ':'
    )
    @DisplayName("주어진 확장자에 맞는 content-type을 반환한다.")
    void determineContentType(String source, String expected) {
        ContentType contentType = ContentType.determineContentType(source);

        assertThat(contentType.getName()).startsWith(expected);
        assertThat(contentType.getName()).endsWith(";charset=utf-8");
    }
}
