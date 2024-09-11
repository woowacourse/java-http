package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MimeTypeTest {

    @Test
    void MimeType_객체를_생성한다() {
        // given
        String fileName = "index.html";

        // when
        MimeType mimeType = MimeType.from(fileName);

        // then
        assertThat(mimeType).isEqualTo(MimeType.HTML);
    }

    @Test
    void 지원하지_않는_파일_확장자일_경우_예외가_발생한다() {
        // given
        String fileName = "index.jsp";

        // when & then
        assertThatThrownBy(() -> MimeType.from(fileName))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 파일 확장자 입니다. fileName: index.jsp");
    }
}
