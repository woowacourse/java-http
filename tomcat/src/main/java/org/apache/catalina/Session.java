package org.apache.catalina;

import com.techcourse.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class Session {

    private final LocalDateTime createdAt;
    private final String id;
    private final User user;

    private Session(String id, LocalDateTime createdAt, User user) {
        this.id = id;
        this.createdAt = createdAt;
        this.user = user;
    }

    public static Session getInstance(User user) {
        return new Session(UUID.randomUUID().toString(), LocalDateTime.now(), user);
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
}
