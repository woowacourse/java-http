package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("확장자 테스트")
class ExtensionTest {

    @DisplayName("파일 확장자인지 확인한다.")
    @Test
    void isFileExtension() {
        // given
        String file1 = "index.html";
        String file2 = "search";

        // when & then
        assertAll(
                () -> assertThat(Extension.isFileExtension(file1)).isTrue(),
                () -> assertThat(Extension.isFileExtension(file2)).isFalse()
        );
    }
}
