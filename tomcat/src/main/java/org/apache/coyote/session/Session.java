package org.apache.coyote.session;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, String> user;

    public Session(User user) {
        this.id = UUID.randomUUID().toString();
        this.user = mapUser(user);
    }

    private Map<String, String> mapUser(User user) {
        Map<String, String> mappedUser = new HashMap<>();
        mappedUser.put("account", user.getAccount());
        mappedUser.put("password", user.getPassword());
        return mappedUser;
    }

    public String getId() {
        return id;
    }
}
