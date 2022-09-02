package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FileUtilTest {

    @Test
    void 파일이름에서_확장자를_반환한다() {
        String expected = "html";
        String actual = FileUtil.getExtension("resources/static/index.html");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 확장자가_없는_경우_예외을_반환한다() {
        assertThatThrownBy(() -> FileUtil.getExtension("resources/static/index"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("path가 파일형식과 맞지 않아 확장자를 반환할 수 없습니다.");
    }
}
