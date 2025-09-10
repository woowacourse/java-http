package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.session.Session;

public class SessionCookieFactory {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    public static HttpCookie createSessionCookie(final Session session) {
        return HttpCookie.of(SESSION_COOKIE_NAME, session.getId());
    }
}
