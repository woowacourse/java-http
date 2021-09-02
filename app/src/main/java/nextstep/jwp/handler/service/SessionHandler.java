package nextstep.jwp.handler.service;

import java.util.Objects;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class SessionHandler {

    public static HttpSession getHttpSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = httpRequest.getSessionId();
        if (!Objects.isNull(sessionId) && HttpSessions.contains(sessionId)) {
            return HttpSessions.getSession(sessionId);
        }

        HttpSession httpSession = HttpSession.create();
        HttpSessions.register(httpSession);
        httpResponse.setCookie(HttpSession.SESSION_TYPE, httpSession.getId());
        return httpSession;
    }

    public static Object getSessionValueOrNull(String sessionId, String key) {
        if (!Objects.isNull(sessionId) && HttpSessions.contains(sessionId)) {
            HttpSession session = HttpSessions.getSession(sessionId);
            if (!Objects.isNull(session)) {
                return session.getAttribute(key);
            }
        }
        return null;
    }
}
