package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class HttpSession {

    private static final String USER = "user";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(User user) {
        this.id = UUID.randomUUID().toString();
        setAttribute(USER, user);
        HttpSessions.add(this);
    }

    public String getId() {
        return this.id;
    }

    public User getUser() {
        return (User) getAttribute(USER);
    }

    private void setAttribute(String name, Object value) {
        this.values.put(name, value);
    }

    private Object getAttribute(String name) {
        return this.values.get(name);
    }

    public void invalidate() {
        HttpSessions.remove(this.id);
    }
}


