package nextstep.jwp.http.handler.session;

import java.util.Map;
import nextstep.jwp.http.common.HttpMethod;
import nextstep.jwp.http.common.session.HttpCookie;
import nextstep.jwp.http.common.session.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class LoginSessionHandler implements SessionHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        if (method.isSame("POST")) {
            String sessionId = request.getSession().getId();
            if (!HttpSessions.isValid(sessionId)) {
                HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", sessionId));
                cookie.addCookie(sessionId);
                response.setCookie(cookie);

                HttpSessions.putSession(request.getSession());
            }
        }
    }
}
