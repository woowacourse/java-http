package org.apache.coyote.model.session;

public class Session {

    private final String key;
    private final Object value;

    public Session(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
