package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ContentTypeTest {

    @ParameterizedTest
    @CsvSource({"/, HTML", "/index.html, HTML", "/style.css, CSS"})
    void findContentType(String path, ContentType expected) {
        final ContentType contentType = ContentType.from(path);

        assertThat(contentType).isEqualTo(expected);
    }
}
