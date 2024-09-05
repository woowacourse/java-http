package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("extension과 일치하는 ContentType을 반환한다.")
    @Test
    void successFindTest() {
        assertAll(
                () -> assertThat(ContentType.find("/index.html")).isEqualTo(ContentType.HTML),
                () -> assertThat(ContentType.find("/index.css")).isEqualTo(ContentType.CSS)
        );
    }

    @DisplayName("extension과 일치하는 ContentType이 없으면 예외를 반환한다.")
    @Test
    void failureFindTest() {
        assertThatThrownBy(() -> ContentType.findWithCharset("/index.jazz"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
