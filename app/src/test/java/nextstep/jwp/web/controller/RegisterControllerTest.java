package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.web.db.InMemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/register");
        HttpResponse httpResponse = new HttpResponse();

        controller.doService(httpRequest, httpResponse);
        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void post() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("POST", "/register",
                "account=wannte&password=password&email=test@test.com");
        HttpResponse httpResponse = new HttpResponse();

        controller.doService(httpRequest, httpResponse);
        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.FOUND);
    }

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.clear();
    }
}
