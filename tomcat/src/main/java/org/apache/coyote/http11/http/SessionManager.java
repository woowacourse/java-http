package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.header.HttpCookie;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static HttpCookie createCookie() {
        return new HttpCookie("JSESSIONID", UUID.randomUUID().toString());
    }
}
