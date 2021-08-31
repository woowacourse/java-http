package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ViewResolver;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = new HttpRequest("GET", "/register");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(ViewResolver.resolveView("register"));
    }

    @Test
    void post() throws IOException {
        HttpRequest httpRequest = new HttpRequest("POST", "/register");
        httpRequest.setPayload("account=wannte&password=password&email=test@test.com");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(HttpResponse.redirect("/index.html"));
    }
}
