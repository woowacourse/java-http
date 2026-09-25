package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestLine;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LogoutControllerTest {

    private final LogoutController logoutController = new LogoutController();

    @Test
    void 로그아웃하면_세션을_제거하고_index로_이동한다() throws IOException {
        Session session = loggedInSession();
        HttpRequest request = request("/logout", session.getId());

        HttpResponse response = logoutController.service(request);

        assertThat(SessionManager.findSession(session.getId())).isNull();
        assertThat(response.headers().get("Location"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/index.html");
    }

    private Session loggedInSession() {
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        return session;
    }

    private HttpRequest request(String path, String sessionId) {
        List<String> headers = sessionId == null
                ? List.of()
                : List.of("Cookie: JSESSIONID=" + sessionId);
        return new HttpRequest(
                RequestLine.from("GET " + path + " HTTP/1.1 "),
                HttpHeaders.from(headers),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}
