package nextstep.jwp.web.handler;

import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.session.HttpCookie;

public class SessionHandler implements WebHandler{

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) {
        if (!request.cookie().containsSession()) {
            HttpCookie cookie = response.request().cookie();
            String sessionId = cookie.createSessionId();
            cookie.setSessionId(sessionId);
            response.setCookie(cookie);
        }
    }
}
