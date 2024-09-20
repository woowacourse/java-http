package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessionStorage = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static synchronized String add(Session session) {
        UUID uuid = UUID.randomUUID();
        sessionStorage.put(uuid.toString(), session);

        return uuid.toString();
    }

    public static Session findSession(String id) {
        return sessionStorage.get(id);
    }
}
