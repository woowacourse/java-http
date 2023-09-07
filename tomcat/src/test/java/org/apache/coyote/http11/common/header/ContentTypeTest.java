package org.apache.coyote.http11.common.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    @Test
    @DisplayName("주어진 ContentType을 문자열로 변환한다.")
    void contentTypeToString() {
        // given
        final ContentTypeValue contentTypeValue = ContentTypeValue.TEXT_HTML;
        final ContentType contentType = new ContentType(contentTypeValue);

        final String expect = "Content-Type: text/html;charset=utf-8 ";

        // when
        final String actual = contentType.convertToString();

        // then
        assertThat(actual).isEqualTo(expect);
    }

}
