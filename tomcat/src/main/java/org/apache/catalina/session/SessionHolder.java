package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionHolder implements Manager {

    private static final Map<String, Session> HOLDER = new LinkedHashMap<>();

    @Override
    public void add(final Session session) {
        HOLDER.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return HOLDER.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        HOLDER.remove(session.getId());
    }
}
