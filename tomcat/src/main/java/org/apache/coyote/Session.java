package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//public class Session {
//    private final String sessionId;
//    private final Object sessionUser;
//
//    public Session(Object sessionUser) {
//        this.sessionId = UUID.randomUUID().toString();
//        this.sessionUser = sessionUser;
//    }
//
//    public String getSessionId() {
//        return sessionId;
//    }
//
//    public Object getUser() {
//        return sessionUser;
//    }
//}
public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public static Session emptySession() {
        return new Session("");
    }

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.getOrDefault(name, null);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}