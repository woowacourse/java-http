package org.apache.coyote.http11.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> sessions = new HashMap<>();

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        Set<Entry<String, HttpSession>> entries = sessions.entrySet();
        String key = entries.stream()
                .filter(entry -> entry.getValue() == session)
                .map(Entry::getKey)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다."));

        sessions.remove(key);
    }
}
