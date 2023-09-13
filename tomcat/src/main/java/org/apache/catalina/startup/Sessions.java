package org.apache.catalina.startup;

import common.http.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> idAndSessions = new ConcurrentHashMap<>();

    void add(String id, Session session) {
        idAndSessions.put(id, session);
    }

    Session get(String id) {
        if (!idAndSessions.containsKey(id)) {
            return null;
        }
        return idAndSessions.get(id);
    }

    void remove(String id) {
        idAndSessions.remove(id);
    }

    void clear() {
        idAndSessions.clear();
    }

    boolean get(Session session) {
        return idAndSessions.containsValue(session);
    }
}
