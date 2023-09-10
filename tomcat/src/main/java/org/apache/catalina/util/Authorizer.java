package org.apache.catalina.util;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManger;
import org.apache.coyote.http11.request.HttpRequest;

public class Authorizer {

    private static final Manager sessionManger = new SessionManger();

    private Authorizer() {
    }

    public static Session findSession(final HttpRequest httpRequest) {
        final String sessionId = httpRequest.getSessionId();

        if (sessionId == null) {
            return null;
        }

        return sessionManger.findSession(sessionId);
    }

    public static void addSession(final Session session) {
        final String sessionId = session.getId();

        if (sessionManger.findSession(sessionId) != null) {
            return;
        }

        sessionManger.add(session);
    }
}
