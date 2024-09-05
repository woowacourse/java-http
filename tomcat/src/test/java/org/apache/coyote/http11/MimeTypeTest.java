package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.MimeType;
import org.apache.coyote.util.FileExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MIME 타입 테스트")
class MimeTypeTest {

    @DisplayName("파일 확장자에 맞게 MIME 타입을 반환한다.")
    @Test
    void getMimeTypeByExtension() {
        // given
        FileExtension extension = FileExtension.CSS;

        // when
        MimeType mimeType = MimeType.from(extension);

        // then
        assertThat(mimeType).isEqualTo(MimeType.CSS);
    }
}
