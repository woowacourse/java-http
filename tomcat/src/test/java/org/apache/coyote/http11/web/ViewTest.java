package org.apache.coyote.http11.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("View 테스트")
class ViewTest {

    @Test
    void 지정한_리소스를_읽는다() throws IOException {
        // given
        final View view = new StaticResourceView("/index.html");

        // when
        final String resource = view.renderView();

        // then
        assertThat(resource).contains("<title>대시보드</title>");
    }

    @Test
    void 지정한_리소스가_존재하지_않으면_예외발생() {
        // given
        final View view = new StaticResourceView("/notfoundasdfas.html");

        // when & then
        Assertions.assertThatThrownBy(view::renderView)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 뷰가 존재하지 않습니다.");
    }
}
