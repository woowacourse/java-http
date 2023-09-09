package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotAllowedMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpExtensionTypeTest {

    @DisplayName("contentType을 올바르게 가져올 수 있다.")
    @Test
    void from() {
        // given
        final String extension = "test.css";
        final String expected = "text/css";

        // when
        final HttpExtensionType actual = HttpExtensionType.from(extension);

        // then
        assertThat(actual.getContentType()).isEqualTo(expected);
    }

    @DisplayName("ExtensionType에 해당하는 확장자가 없으면 예외 처리한다.")
    @Test
    void from_emptyExtension() {
        // given
        final String extension = "EmptyExtension";

        // when & then
        assertThatThrownBy(() -> HttpExtensionType.from(extension))
                .isInstanceOf(NotAllowedMethodException.class)
                .hasMessage("해당하는 Method가 존재하지 않습니다.");
    }
}
