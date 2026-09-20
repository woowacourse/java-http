package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LogoutHandlerTest {

    private final LogoutHandler logoutHandler = new LogoutHandler();

    @Test
    void 로그아웃하면_세션을_제거하고_index로_이동한다() {
        Session session = loggedInSession();
        HttpServletRequest request = request("/logout", session.getId());

        HttpServletResponse response = logoutHandler.handle(request);

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

    private HttpServletRequest request(String path, String sessionId) {
        List<String> headers = sessionId == null
                ? List.of()
                : List.of("Cookie: JSESSIONID=" + sessionId);
        return new HttpServletRequest(
                RequestLine.from("GET " + path + " HTTP/1.1 "),
                HttpHeaders.from(headers),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}
