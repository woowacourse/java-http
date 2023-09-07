package org.apache.coyote.http11.common.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentLengthTest {

    @Test
    @DisplayName("주어진 ContentLength를 문자열로 변환한다.")
    void contentLengthToString() {
        // given
        final ContentLength contentLength = new ContentLength(10);
        final String expect = "Content-Length: 10 ";

        // when
        final String actual = contentLength.convertToString();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
