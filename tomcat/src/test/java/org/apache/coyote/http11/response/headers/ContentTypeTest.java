package org.apache.coyote.http11.response.headers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    @DisplayName("주어진 ContentType을 문자열로 변환한다.")
    void contentTypeToString() {
        // given
        final ContentType contentType = ContentType.TEXT_HTML;

        final String expect = "text/html;charset=utf-8";

        // when
        final String actual = contentType.convertToString();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
