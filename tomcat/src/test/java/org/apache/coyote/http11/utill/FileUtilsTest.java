package org.apache.coyote.http11.utill;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @DisplayName("존재하지 않은 위치의 fileName인 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_위치의_fileName인_경우_예외를_던진다() {
        // given
        String fileName = "mat.txt";

        // when & then
        assertThatThrownBy(() -> FileUtils.readFile(fileName))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("fileName이 들어오면 확장자를 꺼낸다.")
    @Test
    void fileName이_들어오면_확장자를_꺼낸다() {
        // given
        String fileName = "mat.txt";

        // when
        String actual = FileUtils.getFileExtension(fileName);

        // then
        assertThat(actual).isEqualTo(".txt");
    }
    
    @DisplayName("fileName 형식이 잘못된 경우 예외를 던진다.")
    @Test
    void fileName_형식이_잘못된_경우_예외를_던진다() {
        // given
        String fileName = "mat";

        // when & then
        assertThatThrownBy(() -> FileUtils.getFileExtension(fileName))
                .isInstanceOf(InvalidFileNameException.class);
    }
}
