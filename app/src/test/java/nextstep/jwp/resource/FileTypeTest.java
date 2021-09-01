package nextstep.jwp.resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileTypeTest {

    @DisplayName("확장자 명으로 타입을 찾을 수 있는지 확인")
    @Test
    void findByNameTest() {
        //given
        String html = "html";
        //when
        FileType fileType = FileType.findByName(html);
        //then
        assertThat(fileType).isEqualTo(FileType.HTML);
    }
}