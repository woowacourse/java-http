package org.apache.coyote.http11.component.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.component.common.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class MimeTypeTest {

    @ParameterizedTest
    @DisplayName("suffix를 기준으로 MIME 타입을 찾는다.")
    @EnumSource(value = MimeType.class, mode = Mode.EXCLUDE, names = "TEXT_PLAIN")
    void find_mime_type_by_suffix(final MimeType mimeType) {
        // given
        final var resourcePath = "example" + mimeType.getSuffix();

        // when
        final String mimeText = MimeType.find(resourcePath);

        // then
        assertThat(mimeText).isEqualTo(mimeType.getMimeText());
    }

}
