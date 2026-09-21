package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> session;

    public Session(String id, Map<String, Object> session) {
        this.id = id;
        this.session = session;
    }

    public void addUser(String key, User user) {
        session.put(key, user);
    }

    public User getUser(String key) {
        return (User) session.get(key);
    }
}
