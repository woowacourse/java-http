package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpContentTypeTest {

    @Test
    @DisplayName("해당 확장자에 맞는 ContentType을 반환한다.")
    void valueOfCotentType() {
        //given
        String ico = "ico";

        //when
        final HttpContentType httpContentType = HttpContentType.valueOfContentType(ico);

        //then
        Assertions.assertThat(httpContentType.getContentType()).isEqualTo("image/x-icon");
    }
}
