package nextstep.jwp.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilePathTest {

    @DisplayName("파일패스에 파일이 없으면 예외")
    @Test
    void whenFilePathIsNull() {
        //given
        FilePath filePath = new FilePath("noExist.txt", "");
        //when
        //then
        assertThatThrownBy(filePath::path)
            .hasMessageContaining("해당 file은 존재하지 않습니다.");
    }

    @DisplayName("파일패스 객체 생성 확인")
    @Test
    void filePathCreateTest() {
        //given
        FilePath filePath = new FilePath("nextstep.txt", "");
        //when
        //then
        assertThat(filePath.path()).isNotNull();
    }
}