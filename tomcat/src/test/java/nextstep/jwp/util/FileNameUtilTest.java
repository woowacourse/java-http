package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileNameUtilTest {

    @Test
    void 파일이름에서_확장자를_반환한다() {
        String expected = "html";
        String actual = FileNameUtil.getExtension("resources/static/index.html");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 확장자가_없는_경우_html을_반환한다() {
        String expected = "html";
        String actual = FileNameUtil.getExtension("resources/static/index");

        assertThat(actual).isEqualTo(expected);
    }
}
