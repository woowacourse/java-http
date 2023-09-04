package org.apache.coyote.session;

import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.SessionManager;

public class Session {

    private SessionManager sessionManager = new SessionManager();
    private Map<String, String> attributes = new HashMap<>();
    private String id;

    public Session() {
        this.id = randomUUID().toString();
        sessionManager.add(this);
    }

    public Session(String id) {
        this.id = id;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.getOrDefault(key, null);
    }

    public String id() {
        return id;
    }
}
