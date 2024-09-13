package org.apache.catalina.session;

import com.techcourse.model.User;

public class Session {

    private final User user;

    public Session(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
