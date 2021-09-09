package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    @DisplayName("정상 View 요청")
    void resolveView() {
        String viewName = "index";
        String url = "static/" + viewName + ".html";

        View view = ViewResolver.resolveView(viewName);

        assertThat(view).isEqualTo(new View(url));
    }

    @Test
    @DisplayName("존재하지 않는 View 요청")
    void resolveNotExistingView() {
        String viewName = "notExisting";

        assertThatThrownBy(() -> ViewResolver.resolveView(viewName)).isInstanceOf(NotFoundException.class);
    }
}
