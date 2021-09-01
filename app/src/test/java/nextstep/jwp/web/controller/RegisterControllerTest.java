package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.web.db.InMemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/register");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(ViewResolver.resolveView("register"));
    }

    @Test
    void post() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("POST", "/register",
                "account=wannte&password=password&email=test@test.com");

        String actual = controller.doService(httpRequest);
        assertThat(actual).isEqualTo(HttpResponse.redirect("/index.html"));
    }

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.clear();
    }
}
