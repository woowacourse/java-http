package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.ViewResolver;
import org.junit.jupiter.api.Test;

class HomeControllerTest {
    private final Controller controller = new HomeController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(ViewResolver.resolveView("index"));
    }

    @Test
    void post() {
        HttpRequest httpRequest = Fixture.httpRequest("POST", "/");

        assertThatThrownBy(
                () -> controller.doService(httpRequest)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
