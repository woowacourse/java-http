package nextstep.jwp.webserver;

import nextstep.jwp.application.model.User;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private static final String USER = "user";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public User getUser() {
        return (User) getAttribute(USER);
    }

    public void setUser(User user) {
        setAttribute(USER, user);
    }

    public void invalidate() {
        // todo: 세션 만료 미구현
    }
}
