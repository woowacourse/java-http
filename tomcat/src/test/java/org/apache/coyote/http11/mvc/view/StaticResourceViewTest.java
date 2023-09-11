package org.apache.coyote.http11.mvc.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.apache.coyote.http11.view.StaticResourceView;
import org.apache.coyote.http11.view.View;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("StaticResourceView 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StaticResourceViewTest {

    @Test
    void 지정한_리소스를_읽는다() throws IOException {
        // given
        final View view = StaticResourceView.of("/index.html");

        // when
        final String resource = view.renderView();

        // then
        assertThat(resource).contains("<title>대시보드</title>");
    }

    @Test
    void 지정한_리소스의_확장자에_맞는_컨텐츠_타입값을_가진다() {
        // given
        final String resource = "/css/styles.css";

        // when
        final View view = StaticResourceView.of(resource);

        // then
        assertThat(view.getContentType()).isEqualTo("text/css");
    }

    @Test
    void 존재하지_않는_리소스_접근시_예외발생() {
        // given
        final String resourcePath = "/not_found.html";
        final StaticResourceView staticResourceView = StaticResourceView.of(resourcePath);

        // when & then
        assertThatThrownBy(staticResourceView::renderView)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource not found: " + resourcePath);
    }
}
