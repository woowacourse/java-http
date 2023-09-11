package common.http;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @Test
    void 확장자명으로_Content_Type을_찾는다() {
        // given
        String html = "html";
        String css = "css";
        String js = "js";
        String ico = "ico";

        // expect
        assertThat(ContentType.findByExtension(html)).isEqualTo(ContentType.HTML);
        assertThat(ContentType.findByExtension(css)).isEqualTo(ContentType.CSS);
        assertThat(ContentType.findByExtension(js)).isEqualTo(ContentType.JS);
        assertThat(ContentType.findByExtension(ico)).isEqualTo(ContentType.ICO);
    }

    @Test
    void 유효한_확장자명이_아니면_예외를_반환한다() {
        // given
        String avi = "avi";

        // expect
        assertThatThrownBy(() -> ContentType.findByExtension(avi))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 확장자명 입니다.");
    }

    @Test
    void 경로로_Content_Type을_찾는다 () {
        // given
        String path = "/index.html";

        // expect
        assertThat(ContentType.findByPath(path)).isEqualTo(ContentType.HTML);
    }

    @Test
    void 경로에_확장자가_없으면_예외를_반환한다() {
        // given
        String path = "/로이스의은밀한사생활";

        // expect
        assertThatThrownBy(() -> ContentType.findByPath(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일의 확장자명이 없습니다.");
    }

    @Test
    void 경로의_확장자가_유효하지_않으면_예외를_반환한다() {
        // given
        String path = "/로이스의은밀한사생활.avi";

        // expect
        assertThatThrownBy(() -> ContentType.findByPath(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 확장자명 입니다.");
    }
}
