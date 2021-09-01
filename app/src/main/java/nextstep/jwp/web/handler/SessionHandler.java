package nextstep.jwp.web.handler;


import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.session.HttpCookie;
import nextstep.jwp.web.http.session.HttpSessions;

public class SessionHandler implements WebHandler {

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) {
        if (request.session().isNew()) {
            request.session().isNewToFalse();
            HttpCookie cookie = response.request().cookie();
            cookie.setSessionId(HttpSessions.createSession().getId());
            response.setCookie(cookie);
        }
    }
}
