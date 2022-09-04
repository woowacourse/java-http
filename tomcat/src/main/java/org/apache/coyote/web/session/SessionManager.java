package org.apache.coyote.web.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public static final String JSESSIONID = "JSESSIONID";

    private static Map<String, Session> store = new ConcurrentHashMap<>();

    public static Cookie createCookie() {
        String id = UUID.randomUUID().toString();

        return new Cookie(JSESSIONID, id);
    }

    public static void addSession(final String id, final Session session) {
        store.put(id, session);
        System.out.println(store);
    }

    public static Optional<Session> findSession(final String id) {
        if (store.containsKey(id)) {
            return Optional.of(store.get(id));
        }
        return Optional.empty();
    }

    public static void remove(final String id) {
        store.remove(id);
    }
}
