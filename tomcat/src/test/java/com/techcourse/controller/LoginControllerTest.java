package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.IOException;
import com.techcourse.model.User;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    void 로그인에_성공한_경우_쿠키를_설정한다() throws IOException {
        HttpRequest request = loginRequest("account=gugu&password=password");

        HttpResponse response = loginController.service(request);

        assertThat(response.headers().get("Set-Cookie"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .startsWith("JSESSIONID=");
    }

    @Test
    void 로그인에_실패한_경우_401_페이지로_이동한다() throws IOException {
        HttpRequest request = loginRequest("account=gugu&password=wrongPassword");

        HttpResponse response = loginController.service(request);

        assertThat(response.headers().get("Location"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/401.html");
    }

    @Test
    void 로그인에_성공하면_발급한_세션이_SessionManager에_저장된다() throws IOException {
        HttpRequest request = loginRequest("account=gugu&password=password");

        HttpResponse response = loginController.service(request);

        String sessionId = sessionIdOf(response);
        assertThat(SessionManager.findSession(sessionId)).isNotNull();
    }

    @Test
    void 로그인에_성공하면_발급한_세션에_로그인한_사용자를_담는다() throws IOException {
        HttpRequest request = loginRequest("account=gugu&password=password");

        HttpResponse response = loginController.service(request);

        User gugu = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        Session session = SessionManager.findSession(sessionIdOf(response));
        assertThat(session.getAttribute("user")).isSameAs(gugu);
    }

    @Test
    void 로그인에_실패하면_세션을_발급하지_않는다() throws IOException {
        HttpRequest request = loginRequest("account=gugu&password=wrongPassword");

        HttpResponse response = loginController.service(request);

        assertThat(response.headers().get("Set-Cookie")).isEmpty();
    }

    @Test
    void 이미_세션이_있으면_새로_발급하지_않고_재사용한다() throws IOException {
        Session existing = new Session(UUID.randomUUID().toString());
        SessionManager.add(existing);
        HttpRequest request = loginRequestWithSession(
                "account=gugu&password=password", existing.getId());

        HttpResponse response = loginController.service(request);

        assertThat(sessionIdOf(response)).isEqualTo(existing.getId());
    }

    private String sessionIdOf(HttpResponse response) {
        return response.headers().get("Set-Cookie")
                .map(cookie -> cookie.substring("JSESSIONID=".length()))
                .orElseThrow();
    }

    private HttpRequest loginRequest(String body) {
        return new HttpRequest(
                RequestLine.from("POST /login HTTP/1.1 "),
                HttpHeaders.from(List.of("Content-Type: " + ContentType.FORM_URLENCODED.value())),
                RequestBody.of(ContentType.FORM_URLENCODED, body));
    }

    private HttpRequest loginRequestWithSession(String body, String sessionId) {
        return new HttpRequest(
                RequestLine.from("POST /login HTTP/1.1 "),
                HttpHeaders.from(List.of(
                        "Content-Type: " + ContentType.FORM_URLENCODED.value(),
                        "Cookie: JSESSIONID=" + sessionId)),
                RequestBody.of(ContentType.FORM_URLENCODED, body));
    }
}
