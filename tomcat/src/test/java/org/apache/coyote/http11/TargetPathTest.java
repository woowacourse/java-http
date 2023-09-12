package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TargetPathTest {

    @Test
    void 자동완성은_확장자가_없으면_기본값_html을_사용한다() {
        assertThat(new TargetPath("/index").autoComplete().getPath()).isEqualTo("/index.html");
    }

    @Test
    void 자동완성은_최상단_루트인_경우_기본_페이지를_사용한다() {
        assertThat(new TargetPath("/").autoComplete().getPath()).isEqualTo("/index.html");
        assertThat(new TargetPath("/hi/hello/").autoComplete().getPath()).isEqualTo("/hi/hello/index.html");
    }

    @Test
    void 다른_경로와_비교한다() {
        TargetPath path = new TargetPath("/index");

        assertThat(path).isEqualTo(new TargetPath("/index"));
        assertThat(path.autoComplete()).isEqualTo(new TargetPath("/").autoComplete());
    }

    @Test
    void 자동완성_후_다른_경로와_비교한다() {
        TargetPath pathWithoutExtension = new TargetPath("/index");

        assertThat(pathWithoutExtension.autoComplete()).isEqualTo(new TargetPath("/index.html").autoComplete());
        assertThat(pathWithoutExtension.autoComplete()).isEqualTo(new TargetPath("/index").autoComplete());
    }

    @Test
    void 확장자를_가져온다() {
        assertThat(new TargetPath("/hi.hello").getExtension()).isEqualTo("hello");
    }
}
