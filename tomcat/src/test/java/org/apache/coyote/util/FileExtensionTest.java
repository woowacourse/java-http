package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("파일 확장자 테스트")
class FileExtensionTest {

    @DisplayName("파일 확장자인지 확인한다.")
    @Test
    void isFileExtension() {
        // given
        String file1 = "index.html";
        String file2 = "search";

        // when & then
        assertAll(
                () -> assertThat(FileExtension.isFileExtension(file1)).isTrue(),
                () -> assertThat(FileExtension.isFileExtension(file2)).isFalse()
        );
    }
}
