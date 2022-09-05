package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.exception.NoSuchContentTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("extension 정보를 기반으로 ContentType으로 변환한다.")
    @Test
    void extension_정보를_기반으로_ContentType으로_변환한다() {
        // given
        String extension = ".html";

        // when
        ContentType actual = ContentType.from(extension);

        // then
        assertThat(actual).isEqualTo(ContentType.HTML);
    }

    @DisplayName("존재하지 않는 contenType인 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_contentType인_경우_예외를_던진다() {
        // given
        String extension = ".asdafrg";

        // when & then
        assertThatThrownBy(() -> ContentType.from(extension))
                .isInstanceOf(NoSuchContentTypeException.class);
    }
}
