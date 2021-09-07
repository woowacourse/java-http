package nextstep.jwp.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nextstep.jwp.Fixture;
import nextstep.jwp.http.entity.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionHandlerTest {

    @Test
    @DisplayName("Set-Cookie 설정")
    void setCookie() {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        HttpResponse httpResponse = new HttpResponse();

        SessionHandler.handle(httpRequest, httpResponse);
        assertTrue(httpResponse.containsHeader("Set-Cookie"));
    }

    @Test
    @DisplayName("등록되지 않은 경우 새로운 Set-Cookie")
    void UnregisteredSessionsRequest() {
        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        httpRequest.httpCookie().addCookie("JSESSIONID", "sessionId");
        HttpResponse httpResponse = new HttpResponse();

        SessionHandler.handle(httpRequest, httpResponse);
        assertTrue(httpResponse.containsHeader("Set-Cookie"));
    }

    @Test
    @DisplayName("등록된 경우 Set-Cookie False")
    void RegisteredSessionsRequest() {
        String sessionId = "sessionId";
        HttpSessions.add(sessionId, new HttpSession(sessionId));

        HttpRequest httpRequest = Fixture.httpRequest("GET", "/login");
        httpRequest.httpCookie().addCookie("JSESSIONID", sessionId);
        HttpResponse httpResponse = new HttpResponse();

        SessionHandler.handle(httpRequest, httpResponse);
        assertFalse(httpResponse.containsHeader("Set-Cookie"));
    }

    @AfterEach
    void tearDown() {
        HttpSessions.clear();
    }
}
