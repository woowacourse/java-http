package org.apache.catalina.startup;

import common.http.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> idAndSessions = new ConcurrentHashMap<>();

    void add(String id, Session session) {
        idAndSessions.put(id, session);
    }

    Session find(String id) {
        if (!idAndSessions.containsKey(id)) {
            return null;
        }
        return idAndSessions.get(id);
    }

    void remove(String id) {
        idAndSessions.remove(id);
    }

    public void clear() {
        idAndSessions.clear();
    }
}
