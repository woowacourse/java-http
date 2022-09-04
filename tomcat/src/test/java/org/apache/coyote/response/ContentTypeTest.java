package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("적절한 contentType을 반환한다.")
    @Test
    void findCorrectContentType() {
        // given
        String requestUrl = "/css/styles.css";

        // when
        final ContentType sut = ContentType.from(requestUrl);

        // then
        assertThat(sut.toString()).isEqualTo("text/css");
    }
}
