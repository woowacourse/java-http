package kokodak.handler;

import kokodak.http.HttpCookie;
import kokodak.http.HttpRequest;
import kokodak.http.Session;
import kokodak.http.SessionManager;

public class SessionArgumentResolver implements ArgumentResolver {

    @Override
    public Object resolve(final HttpRequest httpRequest) {
        final HttpCookie httpCookie = httpRequest.getHttpCookie();
        final String jSessionId = httpCookie.cookie("JSESSIONID");
        if ("".equals(jSessionId)) {
            return new Session(null);
        }
        return SessionManager.findSession(jSessionId);
    }
}
