package servlet.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    private final ViewResolver viewResolver = new ViewResolver();

    @Test
    void view명으로_view를_반환한다() {
        // given
        String viewName = "/index.html";

        // when
        File view = viewResolver.resolveViewName(viewName);

        // then
        assertThat(view.getName()).isEqualTo("index.html");
    }

    @Test
    void 확장자가_없으면_html을_추가해서_view를_반환한다() {
        // given
        String viewName = "/login";

        // when
        File view = viewResolver.resolveViewName(viewName);

        // then
        assertThat(view.getName()).isEqualTo("login.html");
    }

    @Test
    void view가_없으면_null을_반환한다() {
        // given
        String viewName = "/any.html";

        // when
        File view = viewResolver.resolveViewName(viewName);

        // then
        assertThat(view).isNull();
    }
}
