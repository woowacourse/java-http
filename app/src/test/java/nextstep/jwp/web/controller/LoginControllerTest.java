package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = new HttpRequest("GET", "/login");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(ViewResolver.resolveView("login"));
    }

    @Test
    void post() throws IOException {
        String account = "wannte";
        String password = "password";

        InMemoryUserRepository.save(new User(null, account, password, "email@email.com"));

        HttpRequest httpRequest = new HttpRequest("POST", "/login");
        httpRequest.setPayload("account=" + account + "&password=" + password);

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(HttpResponse.found("/index.html"));
    }
}
