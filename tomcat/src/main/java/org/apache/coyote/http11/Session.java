package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Session {

    private static final Session INSTANCE = new Session();
    private final Map<String, User> userMap;

    private Session() {
        this.userMap = new HashMap<>();
    }

    public static Session getInstance() {
        return INSTANCE;
    }

    public void save(String uuid, User user) {
        userMap.put(uuid, user);
    }

    public boolean containsUser(String uuid) {
        return userMap.containsKey(uuid);
    }

    public User getUser(String uuid) {
        return userMap.get(uuid);
    }

    public Set<String> getKeySet() {
        return userMap.keySet();
    }
}
