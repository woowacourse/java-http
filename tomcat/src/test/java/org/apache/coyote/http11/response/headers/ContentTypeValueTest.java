package org.apache.coyote.http11.response.headers;

import org.apache.coyote.http11.common.header.ContentTypeValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeValueTest {

    @Test
    @DisplayName("주어진 ContentTypeValue를 문자열로 변환한다.")
    void contentTypeToString() {
        // given
        final ContentTypeValue contentTypeValue = ContentTypeValue.TEXT_HTML;

        final String expect = "text/html;charset=utf-8";

        // when
        final String actual = contentTypeValue.convertToString();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
