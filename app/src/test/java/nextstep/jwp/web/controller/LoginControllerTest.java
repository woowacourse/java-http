package nextstep.jwp.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import nextstep.jwp.Fixture;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.entity.HttpSession;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final Controller controller = new LoginController();

    @Test
    void get() throws IOException {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        HttpResponse httpResponse = new HttpResponse();

        controller.doService(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getWhenLoggedIn() throws IOException {
        User user = 유저_저장됨("wannte", "password");

        String sessionId = "sessionId";
        HttpSession session = new HttpSession(sessionId);
        session.setAttribute("user", user);
        HttpSessions.add(sessionId, session);

        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login", session);
        HttpResponse httpResponse = new HttpResponse();

        controller.doService(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.FOUND);
        assertFalse(httpResponse.httpHeaders().hasHeaderName("Set-Cookie"));
    }

    @Test
    void post() throws IOException {
        String account = "wannte";
        String password = "password";

        유저_저장됨(account, password);

        HttpRequest httpRequest = Fixture.httpRequest("POST", "/login", "account=" + account + "&password=" + password);
        HttpResponse httpResponse = new HttpResponse();

        controller.doService(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.FOUND);
        assertTrue(httpResponse.httpHeaders().hasHeaderName("Set-Cookie"));
    }

    private User 유저_저장됨(String account, String password) {
        User user = new User(null, account, password, "email@email.com");
        InMemoryUserRepository.save(user);

        return user;
    }

    @AfterEach
    void tearDown() {
        HttpSessions.clear();
    }
}
