package nextstep.jwp.web.handler;

import java.util.Map;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.session.HttpCookie;
import nextstep.jwp.web.http.session.HttpSessions;

public class SessionHandler implements WebHandler {

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) {
        if (!HttpSessions.isValid(request.session().getId())) {
            HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", request.session().getId()));
            cookie.setSessionId(request.session().getId());
            response.setCookie(cookie);

            HttpSessions.putSession(request.session());
        }
    }
}
