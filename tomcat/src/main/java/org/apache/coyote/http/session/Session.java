package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session {

    private static final Logger log = LoggerFactory.getLogger(Session.class);

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        log.info("name={}", name);
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        log.info("name={}, value={}", name, value);
        values.put(name, value);
    }
}
