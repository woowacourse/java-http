package org.apache.coyote.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MimeTypeTest {

    @Test
    @DisplayName("잘못된 확장자로 찾는 경우 예외를 발생한다.")
    void validateMimeType() {
        String wrong = ".hahaha";

        assertThatThrownBy(() -> MimeType.getContentTypeFromExtension(wrong))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
