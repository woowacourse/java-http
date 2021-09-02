package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        HttpResponse httpResponse = HttpResponse.empty();

        controller.doService(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void post() throws IOException {
        String account = "wannte";
        String password = "password";

        InMemoryUserRepository.save(new User(null, account, password, "email@email.com"));

        HttpRequest httpRequest = Fixture.httpRequest("POST", "/login", "account=" + account + "&password=" + password);
        HttpResponse httpResponse = HttpResponse.empty();

        controller.doService(httpRequest, httpResponse);
        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.FOUND);
    }
}
