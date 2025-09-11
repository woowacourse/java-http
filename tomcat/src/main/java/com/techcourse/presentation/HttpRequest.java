package com.techcourse.presentation;

import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;

public record HttpRequest(
        String method,
        String path,
        String protocol,
        Map<String, String> params,
        Map<String, String> headers
) {

    public Session getSession(final boolean create) {
        final HttpCookie httpCookie = new HttpCookie(this);

        if (httpCookie.hasAttribute("JSESSIONID")) {
            final String token = httpCookie.getAttribute("JSESSIONID");
            final Session session = SessionManager.getInstance().findSession(token);
            if (session != null) {
                return session;
            }
        }

        if (create) {
            final UUID token = UUID.randomUUID();
            final Session session = new Session(token.toString());
            SessionManager.getInstance().add(session);
            return session;
        }

        return null;
    }
}
