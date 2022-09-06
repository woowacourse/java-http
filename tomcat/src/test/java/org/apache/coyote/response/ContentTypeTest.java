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

    @DisplayName("확장자가 없는 경우 .html을 기본으로 반환한다.")
    @Test
    void noExtension() {
        // given
        String requestUrl = "/index";

        // when
        final ContentType sut = ContentType.from(requestUrl);

        // then
        assertThat(sut.toString()).isEqualTo("text/html");
    }
}
