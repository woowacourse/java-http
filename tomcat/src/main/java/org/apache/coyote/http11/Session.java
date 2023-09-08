package org.apache.coyote.http11;

public class Session {

    private final String id;

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
