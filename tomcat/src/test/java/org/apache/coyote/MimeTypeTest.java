package org.apache.coyote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("파일 이름으로 MIME 타입 판별")
class MimeTypeTest {

    @Test
    @DisplayName("확장자로 MIME 타입을 판별한다")
    void fromFileName() {
        assertAll(
                () -> assertThat(MimeType.fromFileName("index.html")).isEqualTo(MimeType.TEXT_HTML),
                () -> assertThat(MimeType.fromFileName("styles.css")).isEqualTo(MimeType.TEXT_CSS),
                () -> assertThat(MimeType.fromFileName("scripts.js")).isEqualTo(MimeType.TEXT_JAVASCRIPT),
                () -> assertThat(MimeType.fromFileName("logo.svg")).isEqualTo(MimeType.IMAGE_SVG)
        );
    }

    @Test
    @DisplayName("점이 여러 개면 마지막 확장자를 쓴다")
    void fromFileNameWithMultipleDots() {
        assertThat(MimeType.fromFileName("chart.min.js")).isEqualTo(MimeType.TEXT_JAVASCRIPT);
    }

    @Test
    @DisplayName("확장자는 대소문자를 구분하지 않는다")
    void fromFileNameIgnoresCase() {
        assertThat(MimeType.fromFileName("INDEX.HTML")).isEqualTo(MimeType.TEXT_HTML);
    }

    @Test
    @DisplayName("지원하지 않거나 확장자가 없는 파일이면 예외가 발생한다")
    void unsupportedFileName() {
        assertAll(
                () -> assertThatThrownBy(() -> MimeType.fromFileName("favicon.ico"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> MimeType.fromFileName("README"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> MimeType.fromFileName("archive."))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}
