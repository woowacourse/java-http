package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotAllowedExtensionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionTypeTest {

    @DisplayName("contentType을 올바르게 가져올 수 있다.")
    @Test
    void from() {
        // given
        final String extension = "test.css";
        final String expected = "text/css";

        // when
        final ExtensionType actual = ExtensionType.from(extension);

        // then
        assertThat(actual.getContentType()).isEqualTo(expected);
    }

    @DisplayName("ExtensionType에 해당하는 확장자가 없으면 예외 처리한다.")
    @Test
    void from_emptyExtension() {
        // given
        final String extension = "EmptyExtension";

        // when & then
        assertThatThrownBy(() -> ExtensionType.from(extension))
                .isInstanceOf(NotAllowedExtensionException.class)
                .hasMessage("해당하는 Extension이 존재하지 않습니다.");
    }
}
