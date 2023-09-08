package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> sessions;

    public Session(final String id) {
        this.id = id;
        this.sessions = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return sessions.get(name);
    }

    public void setAttribute(final String name, final Object value){
        sessions.put(name,value);
    }
}
