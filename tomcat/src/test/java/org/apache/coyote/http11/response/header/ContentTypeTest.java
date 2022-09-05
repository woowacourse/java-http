package org.apache.coyote.http11.response.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @DisplayName("파일 경로로 알맞은 ContentType 객체를 찾을 수 있다.")
    @ParameterizedTest
    @CsvSource({"foo.html,text/html", "foo.ico,image/x-icon", "foo.svg,image/svg+xml"})
    void of(final String path, final String contentType) {
        final ContentType actual = ContentType.of(path);

        assertThat(actual.toString()).contains(contentType);
    }

    @DisplayName("해당하는 ContentType 객체를 찾을 수 없을 때는 기본값으로 HTML을 응답한다.")
    @Test
    void of_ifNotMatched() {
        final ContentType actual = ContentType.of("");

        assertThat(actual).isEqualTo(ContentType.HTML);

    }
}
