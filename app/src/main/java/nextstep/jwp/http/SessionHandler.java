package nextstep.jwp.http;

import nextstep.jwp.http.entity.HttpCookie;

public class SessionHandler {
    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse) {

        if (httpRequest.httpCookie().containsSession() &&
                HttpSessions.containsKey(httpRequest.httpCookie().getSession()))
            return;
        httpResponse.setCookie(HttpCookie.of(httpRequest.httpSession()));
    }
}
