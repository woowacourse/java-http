package org.apache.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MimeTypeTest {

    @Nested
    @DisplayName("문자열에 해당하는  MimeType 반환")
    class getMimeType {

        @Test
        @DisplayName("문자열에 해당하는  MimeType 반환")
        void getMimeType() {
            assertThat(MimeType.getMimeType("text/html")).isEqualTo(MimeType.TEXT_HTML);
        }

        @Test
        @DisplayName("문자열에 해당하는  MimeType 반환")
        void getMimeTypeWhenInvalidType() {
            assertThatThrownBy(() -> MimeType.getMimeType("textasdfadf"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 MimeType 입니다.");
        }
    }
}
