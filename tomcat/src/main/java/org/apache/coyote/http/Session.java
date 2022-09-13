package org.apache.coyote.http;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> attributes;
    private LocalDateTime lastAccessedTime;

    public Session(String id) {
        this.id = id;
        this.attributes = new HashMap<>();
        this.lastAccessedTime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public boolean isLoggedInUser(){
        return SessionManager.findSession(id).isPresent() && attributes.get("user") != null;
    }

    public void changeLastAccessedTime(){
        this.lastAccessedTime = LocalDateTime.now();
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(lastAccessedTime.plusMinutes(30));
    }

}
