package com.techcourse.auth.session;

import com.techcourse.user.model.User;

import java.util.UUID;

public class Session {

    private final String id = UUID.randomUUID().toString();
    private final Long userId;

    public Session(long userId) {
        this.userId = userId;
    }

    public static Session fromUser(User user) {
        return new Session(user.getId());
    }

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
