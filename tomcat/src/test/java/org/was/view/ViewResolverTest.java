package org.was.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    void 뷰_이름으로_뷰를_조회() throws IOException {
        // given
        String viewName = "/login.html";
        ViewResolver viewResolver = ViewResolver.getInstance();

        // when
        View actual = viewResolver.getView(viewName);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + viewName);
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual.getContent()).isEqualTo(expected);
    }

    @Test
    void 뷰_이름으로_찾을_수_없는_경우_null_반환() throws IOException {
        // given
        String viewName = "/nonExistentResource";
        ViewResolver viewResolver = ViewResolver.getInstance();

        // when
        View actual = viewResolver.getView(viewName);

        // then
        assertThat(actual).isNull();
    }
}
