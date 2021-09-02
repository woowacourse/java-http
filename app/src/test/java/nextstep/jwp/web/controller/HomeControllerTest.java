package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.entity.HttpStatus;
import org.junit.jupiter.api.Test;

class HomeControllerTest {
    private final Controller controller = new HomeController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/");
        HttpResponse httpResponse = HttpResponse.empty();
        controller.doService(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void post() {
        HttpRequest httpRequest = Fixture.httpRequest("POST", "/");
        HttpResponse httpResponse = HttpResponse.empty();

        assertThatThrownBy(
                () -> controller.doService(httpRequest, httpResponse)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
