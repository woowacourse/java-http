package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> sessionStorage;

    public Session() {
        this.sessionStorage = new HashMap<>();
    }

    public Object findValue(String name) {
        return sessionStorage.get(name);
    }

    public void store(String name, Object value) {
        sessionStorage.put(name, value);
    }
}
