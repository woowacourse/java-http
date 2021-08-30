package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidFileExtensionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("확장자명을 통해 ContentType 탐색시")
    @Nested
    class findByExtension {

        @DisplayName("탐색에 성공하면 ContentType을 반환한다.")
        @Test
        void Success() {
            assertThat(ContentType.findByExtension("html")).isEqualTo(ContentType.HTML);
            assertThat(ContentType.findByExtension("css")).isEqualTo(ContentType.CSS);
            assertThat(ContentType.findByExtension("js")).isEqualTo(ContentType.JS);
            assertThat(ContentType.findByExtension("ico")).isEqualTo(ContentType.ICO);
            assertThat(ContentType.findByExtension("svg")).isEqualTo(ContentType.SVG);
        }

        @DisplayName("탐색에 실패하면 예외가 발생한다.")
        @Test
        void Exception() {
            assertThatThrownBy(() -> ContentType.findByExtension("wow"))
                .isExactlyInstanceOf(InvalidFileExtensionException.class);
        }
    }
}