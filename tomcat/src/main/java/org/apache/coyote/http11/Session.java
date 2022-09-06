package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final Map<String, Object> attributes;

    public Session(final String id) {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void addAttribute(final String key, final Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(final String key) {
        return attributes.get(key);
    }
}
