package org.apache.coyote.web.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public static final String JSESSIONID = "JSESSIONID";

    private static Map<String, Session> store = new ConcurrentHashMap<>();

    public static Cookie createCookie() {
        String id = UUID.randomUUID().toString();

        return new Cookie(JSESSIONID, id);
    }

    public static Session findSession(final String id) {
        return store.get(id);
    }

    public static void remove(final String id) {
        store.remove(id);
    }
}
