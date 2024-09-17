package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.constant.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MimeTypeTest {

    @DisplayName("파일 경로가 주어지면 파일에 해당하는 MIME 타입을 반환한다.")
    @Test
    void should_returnMimeType_when_filePathGiven() {
        // given
        String filePath = "/index.html";

        // when
        MimeType mimeType = MimeType.find(filePath);

        // then
        assertThat(mimeType).isEqualTo(MimeType.HTML);
    }

    @DisplayName("유효하지 않은 파일 경로가 주어지면 예외를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"/index", "/index.extension"})
    void should_throwException_when_invalidFilePathGiven(String filePath) {
        // given & when & then
        assertThatThrownBy(() -> MimeType.find(filePath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 파일 경로입니다.");
    }
}
