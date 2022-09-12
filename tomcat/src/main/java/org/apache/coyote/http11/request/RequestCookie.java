package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.exception.ParameterNotFoundException;

public class RequestCookie {

    private static final String HEADER_NAME = "Cookie";

    private final Params cookies;

    public RequestCookie(final Params cookies) {
        this.cookies = cookies;
    }

    public static RequestCookie parse(final RequestHeader header) {
        final Params cookies = parseCookie(header);
        return new RequestCookie(cookies);
    }

    private static Params parseCookie(final RequestHeader header) {
        return header.find(HEADER_NAME)
                .map(Params::parse)
                .orElse(Params.empty());
    }

    public boolean existSession() {
        try {
            final String sessionId = cookies.find(Session.JSESSIONID);
            return SessionManager.exist(sessionId);

        } catch (final ParameterNotFoundException e) {
            return false;
        }
    }
}
