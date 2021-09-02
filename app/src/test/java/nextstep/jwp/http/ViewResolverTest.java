package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    @DisplayName("정상 View 요청")
    void resolveView() throws IOException {
        String viewName = "index";

        String url = "static/" + viewName + ".html";
        final URL resource = ViewResolver.class.getClassLoader().getResource(url);
        final Path path = new File(Objects.requireNonNull(resource).getPath()).toPath();
        String html = Files.readString(path);
        String expected = HttpResponse.ok("text/html", html);

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
