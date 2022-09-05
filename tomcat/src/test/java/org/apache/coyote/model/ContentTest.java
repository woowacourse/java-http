package org.apache.coyote.model;

import org.apache.coyote.exception.NotFoundContentTypeException;
import org.apache.coyote.model.Content;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentTest {

    @Test
    @DisplayName("확장자에 맞는 contentType을 반환한다.")
    void checkReturnExtensionForContentType() {
        // given
        String extension = "html";

        // when
        String contentType = Content.getType(extension);

        // then
        assertThat(contentType).isEqualTo("text/html");
    }

    @Test
    @DisplayName("등록되지 않은 확장자를 호출할 경우 예외가 발생한다.")
    void checkNonContentType() {
        assertThatThrownBy(() -> Content.getType("hwp"))
                .isInstanceOf(NotFoundContentTypeException.class);
    }
}
