package org.apache.coyote.http11.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Body {

    private final Map<String, String> values = new ConcurrentHashMap<>();

    protected Body(final String plaintext) {
        consume(plaintext);
    }

    protected abstract void consume(final String plaintext);

    public String get(final String key) {
        return values.get(key);
    }

    public void add(final String key, final String value) {
        values.put(key, value);
    }
}
